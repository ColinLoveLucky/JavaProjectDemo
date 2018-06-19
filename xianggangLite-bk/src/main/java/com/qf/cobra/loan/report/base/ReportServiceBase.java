package com.qf.cobra.loan.report.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.qf.cobra.mongo.MongoOperate;
import com.qf.cobra.pojo.LoanApply;

@Service("reportServiceBase")
@Primary
public  class ReportServiceBase implements IReportService{
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ReportServiceBase.class);
	@Value("${NEW.DAC.QUERYDATE.URL}")
	private String reportDetailUrl;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private MongoOperate mongoTemplate;
	
	@Override
    public String reportDetail(String requestJson) {
		try {
			LOGGER.info("获得征信报告信息入参:{}", requestJson);
			HttpHeaders headers = new HttpHeaders();
			MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
			headers.setContentType(type);
			HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
			String postForObject = restTemplate.postForObject(reportDetailUrl, entity, String.class);
			LOGGER.info("获得聚信立互联网资讯报告信息原始返回:{}", postForObject);
			return postForObject;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 根据属性attribute判断contactList是否包含contacts中的属性
	 * @param contacts	传入属性的list
	 * @param contactList	需要判断的list是否包含传入属性的contacts
	 * @param attribute		属性名称
	 * @return
	 */
	@SuppressWarnings({ "null", "unchecked" })
	public List<Object> containList(List<Object> contacts,List<Object> contactList,String attribute){
		LOGGER.info("进入reportServiceBase的containList方法");
		if(contactList == null && contactList.size() ==0){
			return new ArrayList<Object>();
		}
		if(contacts == null && contacts.size() ==0){
			return new ArrayList<Object>();
		}
		//排序
		Collections.sort(contactList, new MyComparator());
		List<Object> lists = new ArrayList<Object>();
		for (Object contact : contacts) {
			Map<String, String>  contactMap=(Map<String, String>)contact;
			for (int i = 0; i < contactList.size(); i++) {
				Map<String, Object>  objectMap=(Map<String, Object>)contactList.get(i);
				if(objectMap.get(attribute).equals(contactMap.get("phone"))){
					objectMap.put("num_ci", i+1);
					objectMap.put("relation", contactMap.get("relation"));
					objectMap.put("name", contactMap.get("name"));
					lists.add((Object)objectMap);
				}
			}
		}
		LOGGER.info("reportServiceBase的containList方法结束,返回值为{}",lists);
		return lists;
		
	}
	@SuppressWarnings("unchecked")
    class MyComparator implements Comparator<Object>
    {
        //这里的o1和o2就是list里任意的两个对象，然后按需求把这个方法填完整就行了
        @Override
        public int compare(Object o1, Object o2)
        {
			Map<String, Object>  objectMap1=(Map<String, Object>)o1;
			Map<String, Object>  objectMap2=(Map<String, Object>)o2;
			if((Double)objectMap1.get("call_len")>(Double)objectMap2.get("call_len")){
				return -1;
			}
			return 0;
        }
    }

	@Override
	public String convertDetail(Map<String, Object> jsonMap, Map<String, Object> requestBody) throws Exception {
		return null;
	}

	@Override
	public Map<String, Object> getRequestBodyMap(Map<String, String> map) {
		map.remove("token");
		LoanApply loanApply = (LoanApply) mongoTemplate.findOne(map, LoanApply.class);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if(loanApply !=null){
			Map<String, Object> loanData = loanApply.getLoanData();
			Map<String, Object> personalInfo = MapUtils.getMap(loanData,"personalInfo");
			List<Map<String, Object>> contactInfo = (List<Map<String, Object>>) loanData.get("contactInfo");
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> contactMap : contactInfo) {
				Map<String, Object> userMap = new HashMap<String, Object>();
				userMap.put("name", contactMap.get("name"));
				userMap.put("phone", contactMap.get("phone"));
				userMap.put("relation", Relation.getByCode(contactMap.get("relation").toString()).getMessage());
				list.add(userMap);
			}
			resultMap.put("phoneNo", personalInfo.get("mobilePhone"));
			resultMap.put("idCard", personalInfo.get("idCard"));
			resultMap.put("contacts", list);
		}
		return resultMap;
	}

	private enum Relation {
		RELATION_SHIPREL_PAR("relationshipRelPar", "父母"), RELATION_SHIPREL_CON(
				"relationshipRelCon", "配偶"), RELATION_SHIPREL_KID("relationshipRelKid",
				"子女"), RELATION_SHIPREL_FRI("relationshipRelFri", "朋友"), RELATION_SHIPREL_MAT(
				"relationshipRelMat", "同事"), RELATION_SHIPREL_REL(
				"relationshipRelRel", "亲属"), RELATION_SHIPREL_OTHER(
				"relationshipRelOther", "其他"), RELATION_SHIPREL_DRI(
				"relationshipRelDri", "司机"), RELATION_SHIPREL_RELANDFRI(
						"relationshipRelRelAndFri", "亲属朋友");
		private String code;
		private String message;

		private Relation(String code, String message) {
			this.code = code;
			this.message = message;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
		
	    public static Relation getByCode(String code) {
	        for (Relation item : Relation.values()) {
	            if (item.getCode().equals(code)) {
	                return item;
	            }
	        }
	        return null;
	    }

	}

}
