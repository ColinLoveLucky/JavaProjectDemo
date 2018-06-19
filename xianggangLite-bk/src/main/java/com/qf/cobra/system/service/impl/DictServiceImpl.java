package com.qf.cobra.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.qf.cobra.pojo.Dict;
import com.qf.cobra.qapp.service.impl.QappServiceImpl;
import com.qf.cobra.system.service.IDictService;
import com.qf.cobra.util.HttpClientUtil;
import com.qf.cobra.util.JsonUtil;

@Service
public class DictServiceImpl implements IDictService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DictServiceImpl.class);

	@Autowired
	private MongoTemplate mongoTemplate;
	@Value("${QA.base.url}")
	private String qaUrl;
	@Value("${QA.department.action}")
	private String getDepartmentAction;
	@Value("${QA.city.action}")
	private String getCityAction;
	@Value("${qyjapi.getDictCity}")
	private String getCityUrl;
	@Autowired
	private QappServiceImpl qappServiceImpl;

	@Override
	public void saveDict(Dict dict) {
		mongoTemplate.save(dict);
	}

	@Override
	public Dict getDict(String code) {
		List<Dict> dictList = mongoTemplate.find(Query.query(Criteria.where("value").is(code)), Dict.class);
		if (!CollectionUtils.isEmpty(dictList)) {
			return dictList.get(0);
		}
		return null;
	}

	@Override
	public List<Dict> findAll() {
		return mongoTemplate.findAll(Dict.class);
	}

	@Override
	public void matchRejectLevel(String result, Map<String, Object> object) {
		if (StringUtils.equals(result, "REJECT") && object != null) {
			String typeCode = MapUtils.getString(object, "typeCode");
			Dict dict = this.getDict("REJECT_CODE");
			String rejectLevel = "";
			if (dict != null) {
				List<Dict> children = dict.getChildren();
				for (int i = 0; i < children.size(); i++) {
					Dict dict2 = children.get(i);
					if (dict2 != null && StringUtils.equalsIgnoreCase(dict2.getValue().toString(), typeCode)) {
						rejectLevel = dict2.getRemark();
					}
				}
			}
			object.put("rejectLevel", rejectLevel);
		}

	}

	private void upsertDict(Dict dict) {
		if (dict != null) {
			Update update = new Update();
			update.set("label", dict.getLabel());
			update.set("value", dict.getValue());
			update.set("remark", dict.getRemark());
			update.set("disabled", dict.getDisabled());
			update.set("children", dict.getChildren());
			mongoTemplate.upsert(Query.query(Criteria.where("value").is(dict.getValue())), update, Dict.class);
		}
	}
	
	private Dict loadAppCityDict() throws Exception {
		Dict dict = new Dict();
		dict.setValue("APP_CITY");
		dict.setLabel("进件城市");
		dict.setDisabled(false);
		LOGGER.info("调用QA查询进件城市");
		String cityResult = HttpClientUtil.doGet(qaUrl + getCityAction + "?code=PUHUI", "UTF-8", null);
		LOGGER.info("QA返回进件城市：" + cityResult);
		List<Map<String, Object>> cityResultList = JsonUtil.convert(cityResult, List.class);
		List<Dict> cityChildren = new ArrayList<Dict>();
		for (Map<String, Object> city : cityResultList) {
			Dict cityDict = new Dict();
			String cityCode = (String) city.get("CityCode");
			cityDict.setLabel((String) city.get("CityName"));
			cityDict.setValue(cityCode);
			cityDict.setDisabled(!(Boolean) city.get("Enable"));
			cityChildren.add(cityDict);
			LOGGER.info("调用QA查询城市营业部：" + cityDict.getLabel());
			String deptResult = HttpClientUtil.doGet(qaUrl + getDepartmentAction + "?cityCode=" + cityCode+"&group=PUHUI", "UTF-8", null);
			LOGGER.info("QA返回城市营业部：{}：【{}】", new Object[]{cityDict.getLabel(), cityResult});
			List<Map<String, Object>> deptResultList = JsonUtil.convert(deptResult, List.class);
			List<Dict> deptChildren = new ArrayList<Dict>();
			for (Map<String, Object> dept : deptResultList) {
				Dict deptDict = new Dict();
				deptDict.setLabel((String) dept.get("FullName"));
				deptDict.setValue((String) dept.get("Code"));
				deptDict.setDisabled("1".equals(dept.get("Enabled")) ? false : true);
				deptChildren.add(deptDict);
			}
			cityDict.setChildren(deptChildren);
		}
		dict.setChildren(cityChildren);
		return dict;
	}

	@Override
	public void refreshDict(String key) throws Exception {
		if ("ALL".equals(key)) {
			List<String> initCodeList = getInitCodeList();
			if (!CollectionUtils.isEmpty(initCodeList)) {
				for (String initCode : initCodeList) {
					loadDict(initCode);
				}
			}
		} else {
			List<String> initCodeList = getInitCodeList();
			if (initCodeList == null || !initCodeList.contains(key)) {
				if (initCodeList == null) {
					initCodeList = new ArrayList<String>();
				}
				String value = "";
				for (String initCode : initCodeList) {
					value += initCode;
					value += ",";
				}
				value += key;
				Update update = new Update();
				update.set("value", "INIT_CODE");
				update.set("label", value);
				mongoTemplate.upsert(Query.query(Criteria.where("value").is("INIT_CODE")), update, Dict.class);
			}
			loadDict(key);
		}
	}

	private void loadDict(String key) throws Exception {
		Dict dict = null;
		if ("APP_CITY".equals(key)) {
			dict = loadAppCityDict();
		} else if ("CITY".equals(key)) {
			dict = loadCityDict(key);
		} else if ("XD_PRD_MAP".equals(key)) {
			dict = loadXdPrdDict(key);
		} else {
//			dict = getDictDetail(key);
		}
		if (dict != null) {
			upsertDict(dict);
		}
	}
	
	private Dict loadXdPrdDict(String key) {
		Dict dict = getDict("XD_PRD_MAP");
		
		if(dict != null){
		   //遍历产品
			List<Dict> children = dict.getChildren();
			for (int i = 0; i < children.size(); i++) {
				Dict prod = children.get(i);
				String productCode = prod.getRemark().split(",")[0];
				
				LOGGER.info("调用QC获取相关产品{}信息，刷新内容",productCode);
				Map<String, Object> prodData = qappServiceImpl.queryProductInterestFromService(productCode);
				if(prodData!=null && prodData.size()>0){
					Map<String,Object> fieldMap = (Map<String,Object>)((Map<String,Object>)prod.getValue()).get("qcFields");
					for(Entry<String,Object> field : fieldMap.entrySet()){
						field.setValue(getProdValue(field.getKey(),prodData));
					}
				}
				
			}
		}
		return dict;
	}
	
	private Object getProdValue(String keys, Map<String, Object> prodMap) {
		if(keys.indexOf("|")!=-1){
			String subkey = keys.substring(0, keys.indexOf("|"));
			Map<String, Object> subprodMap = new HashMap<String, Object>();
			
			if(subkey.indexOf("[")!=-1){
				subkey = subkey.substring(0, subkey.indexOf("["));
				subprodMap = ((List<Map<String, Object>>)prodMap.get(subkey)).get(0);
			}else{
				subprodMap = (Map<String, Object>)prodMap.get(subkey);
			}
			return getProdValue(keys.substring(keys.indexOf("|")+1),subprodMap);
		}else{
			return prodMap.get(keys);
		}
	}

	private Dict loadCityDict(String key) {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		Map<String, Object> dataParam = new HashMap<String, Object>();
		dataParam.put("code", key);
		dataParam.put("dataVersion", "2");
		
		Dict parentDict = null;
		String detailResult;
		try {
			detailResult = HttpClientUtil.doPostWithJson(getCityUrl, dataParam,headers);
//			detailResult = detailResult.replaceAll("DATANAME", "label").replaceAll("DATACODE", "value").replaceAll("DATA", "children");
			LOGGER.error("",  detailResult);
			Map<String, Object> detailMap = JsonUtil.convert(detailResult, Map.class);
			if ("10000".equals(detailMap.get("responseCode"))) {
				List<Map<String, Object>> detailList = (List<Map<String, Object>>) detailMap.get("body");
				if (!CollectionUtils.isEmpty(detailList)) {
					Map<String, Object> detail = detailList.get(0);
					parentDict = new Dict("城市", "CITY");
					parentDict.setChildren(converDict((List<Map<String, Object>>)detail.get("crDataList")));
				}
			}
		} catch (Exception e) {
			LOGGER.error("获取{}字典数据失败", key, e);
		}
		
		return parentDict;
	}
	
	private List<Dict> converDict(List<Map<String, Object>> childrens){
		List<Dict> childs = null;
		if(!CollectionUtils.isEmpty(childrens)){
			childs = new ArrayList<Dict>();
			for(Map<String, Object> children : childrens){
				Dict dict = new Dict((String)children.get("dataName"),(String)children.get("dataCode"));
				dict.setChildren(converDict((List<Map<String, Object>>)children.get("crDataList")));
				childs.add(dict);
			}
		}
		return childs;
	}

	private List<String> getInitCodeList() {
		List<String> initCodeList = null;
		List<Dict> dictList = mongoTemplate.find(Query.query(Criteria.where("value").is("INIT_CODE")), Dict.class);
		if (!CollectionUtils.isEmpty(dictList)) {
			String value = dictList.get(0).getLabel();
			if (value != null && !"".equals(value)) {
				initCodeList = Arrays.asList(value.split(","));
			}
		}
		return initCodeList;
	}
	
}
