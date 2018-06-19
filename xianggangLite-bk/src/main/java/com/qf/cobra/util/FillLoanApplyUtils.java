package com.qf.cobra.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.helpers.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FillLoanApplyUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(FillLoanApplyUtils.class);
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getDefaults() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String definitionJson = IOUtils.toString(new PathMatchingResourcePatternResolver().getResource("classpath:/apply_default_dictionary.json").getInputStream(), "utf-8");
			return mapper.readValue(definitionJson, Map.class);
		} catch (JsonParseException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		} catch (JsonMappingException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public static void fillMaps(Map<String, Object> loanData, Map<String, Object> defaults) {
		if (defaults != null) {
			Set<Entry<String, Object>> entrySet = defaults.entrySet();
			for (Iterator<Entry<String, Object>> it = entrySet.iterator(); it.hasNext();) {
				Entry<String, Object> next = it.next();
				String key = next.getKey();
				if (!loanData.containsKey(key)) {
					loanData.put(key, next.getValue());
				} else {
					Object object = next.getValue();
					if (object instanceof Map) {
						Map<String, Object> outKey = (Map<String, Object>) object;
						Map<String, Object> loanDataOutKey = (Map<String, Object>) loanData.get(key);
						fillMaps(loanDataOutKey, outKey);
					} else if (object instanceof List) {
						if (StringUtils.equalsIgnoreCase(key, "contactInfo")) {
							List<Map<String, Object>> outKey = (List<Map<String, Object>>) object;
							List<Map<String, Object>> loanDataOutKey = (List<Map<String, Object>>) loanData.get(key);
							loanDataOutKey = fillListByDeafaultSort(loanDataOutKey, outKey);
							loanData.put(key, loanDataOutKey);
							for (int i = 0; i < outKey.size(); i++) {
								Map<String, Object> loanDataOutValue = loanDataOutKey.get(i);
								Map<String, Object> OutValue = outKey.get(i);
								fillMaps(loanDataOutValue, OutValue);
							}
						}
					} else {
						if (!loanData.containsKey(key)) {
							loanData.put(key, next.getValue());
						}
					}
				}
			}
		}
	}
	private static List<Map<String, Object>> fillListByDeafaultSort(List<Map<String, Object>> loanDataOutKey, List<Map<String, Object>> outKey) {
		List<Map<String, Object>> defaList = new ArrayList<Map<String, Object>>();
		defaList.addAll(outKey);
		for (int i = 0; i < defaList.size(); i++) {
			Map<String, Object> outKeyValue = defaList.get(i);
			String outContactProperty = (String) outKeyValue.get("contactProperty");
			int index = isContailsKey(outContactProperty, loanDataOutKey);
			if (index != -1) {
				Map<String, Object> loanDataValue = loanDataOutKey.get(index);
				defaList.set(i, loanDataValue);
			}
		}
		return defaList;

	}
	private static int isContailsKey(String outContactProperty, List<Map<String, Object>> outKey) {
		for (int i = 0; i < outKey.size(); i++) {
			Map<String, Object> outKeyValue = outKey.get(i);
			String type = (String) outKeyValue.get("contactProperty");
			if (StringUtils.equalsIgnoreCase(outContactProperty, type)) {
				return i;
			}
		}
		return -1;
	}
}
