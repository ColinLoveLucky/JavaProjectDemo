package com.qf.cobra.qapp.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.qf.cobra.pojo.Dict;
import com.qf.cobra.qapp.QappProperties;
import com.qf.cobra.qapp.service.IQappService;
import com.qf.cobra.system.service.IDictService;
import com.qf.cobra.system.service.impl.DictServiceImpl;
import com.qf.cobra.util.DateUtil;
import com.qf.cobra.util.DictItem;
import com.qf.cobra.util.HttpClientUtil;
import com.qf.cobra.util.JsonUtil;

@Service
public class QappServiceImpl implements IQappService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(QappServiceImpl.class);
	@Autowired
	private QappProperties properties;
	
	@Autowired 
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
	private IDictService iDictService;

	@Override
	public Map<String, Object> queryProductInterest(String productCode) {
		Map<String, Object> map = queryProductInterestFromRedis(productCode);
		
		if(map == null || map.isEmpty()){
			if("xd".equals(properties.getQuerySwitch())){
				map = queryProductInterestFromXDService(productCode);
			}else{
				map = queryProductInterestFromService(productCode);
			}
			
			saveProductInterestToRedis(productCode,map);
		}
		
		return map;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> queryProductInterestFromRedis(String productCode) {
		String json = stringRedisTemplate.opsForValue().get(DictItem.PRODUCT_INTEREST_PREFFIX + productCode);
		if(StringUtils.isNotBlank(json)){
			return (Map<String, Object>)JsonUtil.convert(json, Map.class);
		}
		return null;
	}
	
	public void saveProductInterestToRedis(String productCode,Map<String, Object> map) {
		String json = JsonUtil.convert(map);
		String redisKey = DictItem.PRODUCT_INTEREST_PREFFIX + productCode;
		stringRedisTemplate.opsForValue().set(redisKey, json);
		stringRedisTemplate.expire(redisKey, properties.getCacheTime(), TimeUnit.SECONDS);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> queryProductInterestFromService(String productCode) {
		String url = new StringBuffer()
				.append(properties.getQueryUrl())
				.append("/")
				.append(productCode).toString();
		Map<String, String> headers = new HashMap<String, String>();
		
		Map<String,Object> obj = new HashMap<String, Object>();
		try {
			String result = HttpClientUtil.doGet(url, DictItem.CHARSET_NAME, headers);
			if(StringUtils.isNotBlank(result)){
				Map<String, Object> resultObj = JsonUtil.convert(result,
						Map.class);
				
				List<Map<String, Object>> productInfoList = (List<Map<String, Object>>) MapUtils.getObject(resultObj, "productInfoList",new ArrayList<Map<String, Object>>());
//				Map<String, Object> map  = productInfoList.get(0);
				if(CollectionUtils.isNotEmpty(productInfoList)){
					obj = productInfoList.get(0);
				}
			}
			
			LOGGER.info("产品编号:{},查询产品子类明细-返回结果:{}",productCode,obj);
			
		} catch (Exception e) {
			LOGGER.error("产品编号:{},查询产品子类明细-出现异常",productCode,e);
		}
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryProductInterestFromXDService(String productCode) {
		Dict dict = iDictService.getDict("XD_PRD_MAP");
		Map<String, Object> prodMap = null;
		Dict prod = null;
		if (dict != null) {
			List<Dict> children = dict.getChildren();
			for (int i = 0; i < children.size(); i++) {
				Dict dict2 = children.get(i);
				if (dict2 != null && dict2.getRemark().contains(productCode)) {
					prod = dict2;
					break;
				}
			}
		}
		
		if(null == prod){
			return prodMap;
		}else{
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
			
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("batch_id", DateUtil.formatDateWithPattern(new Date(),"yyyyMMddHHmmssSSS"));
			map.put("interface_id", "QUARKLOAN-API-00040");
			map.put("product_type", "1");
			map.put("product_class", "90");
			map.put("kind_no", prod.getLabel());
			map.put("begin_kind_no", "0");
			map.put("size", "100");
			
			try {
				LOGGER.info("开始获取小贷产品信息:{}", productCode);
				String result = HttpClientUtil.doPostWithJson(properties.getQueryXDUrl(),
						map, headers);
				result = result == null ? "" : result;
				// {"ErrCode":"1001","ErrMsg":null,"BizErrorMsg":"更新条目时出错。有关详细信息，请参见内部异常。","IsBizSuccess":false}
				LOGGER.info("结束获取小贷产品信息:{},请求结果为:{}", productCode, result);
				if(StringUtils.isNotBlank(result)){
					Map<String, Object> resultObj = JsonUtil.convert(result,
							Map.class);
					if("200".equals(MapUtils.getString(resultObj, "code"))){
						List<Map<String, Object>> productInfoList = (List<Map<String, Object>>) MapUtils.getObject(resultObj, "data_list",new ArrayList<Map<String, Object>>());
						if(CollectionUtils.isNotEmpty(productInfoList)){
							prodMap = constructQcProdMap(prod,productInfoList.get(0));
						}
					}
				}
			} catch (Exception e) {
				LOGGER.error("产品编号:{},查询产品子类明细-出现异常",productCode,e);
			}
		}
		return prodMap;
	}
	
	private Map<String,Object> constructQcProdMap(Dict prod, Map<String, Object> productInfo){
		Map<String,Object> prodMap = new HashMap<String, Object>();
		Map<String,Object> prodCfg = (Map<String,Object>)prod.getValue();
		String loanMaturity = prod.getRemark().substring(prod.getRemark().indexOf(",")+1);
		// 费率初始化
		Map<String,Object> fieldMap = (Map<String,Object>)(prodCfg.get("fields"));
		for(Entry<String, Object> field:fieldMap.entrySet()){
			setValue("0.0", ((Map<String, Object>)field.getValue()).get("node").toString(), prodMap);
		}
		prodMap.put("splitFees", new ArrayList<Map<String, Object>>());
		
		for(Entry<String,Object> cfg : prodCfg.entrySet()){
			String key = cfg.getKey();
			if("fields".equals(key)){
				// 费率整理
				fieldMap = (Map<String,Object>)cfg.getValue();
				List<Map<String, Object>> prodfields = (List<Map<String, Object>>) MapUtils.getObject(productInfo, "fee_list",new ArrayList<Map<String, Object>>());
				for(Map<String, Object> prodfield:prodfields){
					if(fieldMap.containsKey(prodfield.get("item_no"))){
						//常规费率 String.valueOf(prodfield.get("rate_scale"))
						Map<String, Object> feeMap = (Map<String, Object>)fieldMap.get(prodfield.get("item_no"));
						if("1".equals(prodfield.get("rate_type"))){
							setValue(calFee(MapUtils.getString(prodfield, "rate_scale"),MapUtils.getString(feeMap, "scale")), feeMap.get("node").toString(), prodMap);
						}else if ("2".equals(prodfield.get("rate_type"))){
							setValue(String.valueOf(prodfield.get("rate_amt")), feeMap.get("node").toString(), prodMap);
						}
						
						// 构造list
						Map<String, Object> subprodMap = new HashMap<String, Object>();
						subprodMap.put("ACCOUNT_CODE", prodfield.get("shareincome_no"));
						subprodMap.put("FEE_CODE", prodfield.get("item_no"));
						subprodMap.put("FEE_NAME", prodfield.get("item_name"));
						subprodMap.put("DEDUCT_THE_WAY", prodfield.get("cut_charge_type"));

						if("1".equals(prodfield.get("rate_type"))){
							subprodMap.put("SPLITTING_RATIO", new BigDecimal(calFee(MapUtils.getString(prodfield, "rate_scale"),"0.01")));//百分比转换
						}else if ("2".equals(prodfield.get("rate_type"))){
							subprodMap.put("SPLITTING_FEE", new BigDecimal(prodfield.get("rate_amt").toString()));
						}
						
						if(!prodMap.containsKey("oldSplitFees")){
							List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
							datas.add(subprodMap);
							prodMap.put("oldSplitFees", datas);
						}else{
							((List<Map<String, Object>>)prodMap.get("oldSplitFees")).add(subprodMap);
						}
					}else if("1".equals(prodfield.get("is_use_item"))&&StringUtils.isNotBlank(String.valueOf(prodfield.get("shareincome_no")))){
						// 构造list
						Map<String, Object> subprodMap = new HashMap<String, Object>();
						subprodMap.put("ACCOUNT_CODE", prodfield.get("shareincome_no"));
						subprodMap.put("FEE_CODE", prodfield.get("item_no"));
						subprodMap.put("FEE_NAME", prodfield.get("item_name"));
						subprodMap.put("DEDUCT_THE_WAY", prodfield.get("cut_charge_type"));

						if("1".equals(prodfield.get("rate_type"))){
							subprodMap.put("SPLITTING_RATIO", new BigDecimal(calFee(MapUtils.getString(prodfield, "rate_scale"),"0.01")));//百分比转换
						}else if ("2".equals(prodfield.get("rate_type"))){
							subprodMap.put("SPLITTING_FEE", new BigDecimal(prodfield.get("rate_amt").toString()));
						}
						
						if(!prodMap.containsKey("splitFees")){
							List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
							datas.add(subprodMap);
							prodMap.put("splitFees", datas);
						}else{
							((List<Map<String, Object>>)prodMap.get("splitFees")).add(subprodMap);
						}
					}
				}
				
				//由于保证金费率=每期账户管理费*期数，所以需手动计算
				String eachLoanFee = MapUtils.getString(MapUtils.getMap(prodMap, "pProduct"), "eachLoanFee", "");
				setValue(calFee(eachLoanFee,loanMaturity),"pProduct|marginRate",prodMap);
			}else if("qcFields".equals(key)){
				fieldMap = (Map<String,Object>)cfg.getValue();
				for(Entry<String,Object> field : fieldMap.entrySet()){
					setValue(field.getValue().toString(),field.getKey(),prodMap);
				}
			}else{
				if(productInfo.containsKey(key)){
					Map<String, Object> feeMap = (Map<String, Object>)cfg.getValue();
					setValue(calFee(MapUtils.getString(productInfo, key),MapUtils.getString(feeMap, "scale")),MapUtils.getString(feeMap, "node"),prodMap);
				}
			}
		}
		
		return prodMap;
	}

	private String calFee(String feeData, String scale) {
		if(StringUtils.isBlank(feeData)||StringUtils.isBlank(scale)){
			return null;
		}
		BigDecimal fee =new BigDecimal(feeData); 
		if(fee.compareTo(BigDecimal.ZERO)>0){
			fee = fee.multiply(new BigDecimal(scale));
		}
		return fee.toString();
	}

	private void setValue(String data, String keys, Map<String, Object> prodMap) {
		if(keys.indexOf("|")!=-1){
			String subkey = keys.substring(0, keys.indexOf("|"));
			Map<String, Object> subprodMap = new HashMap<String, Object>();
			
			if(subkey.indexOf("[")!=-1){
				subkey = subkey.substring(0, subkey.indexOf("["));
				if(!prodMap.containsKey(subkey)){
					List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
					datas.add(subprodMap);
					prodMap.put(subkey, datas);
				}else{
					subprodMap = ((List<Map<String, Object>>)prodMap.get(subkey)).get(0);
				}
			}else{
				if(!prodMap.containsKey(subkey)){
					prodMap.put(subkey, subprodMap);
				}else{
					subprodMap = (Map<String, Object>)prodMap.get(subkey);
				}
			}
			setValue(data,keys.substring(keys.indexOf("|")+1),subprodMap);
		}else{
			prodMap.put(keys, data);
		}
	}
}
