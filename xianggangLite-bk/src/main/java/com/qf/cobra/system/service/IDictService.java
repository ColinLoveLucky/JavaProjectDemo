package com.qf.cobra.system.service;

import java.util.List;
import java.util.Map;

import com.qf.cobra.pojo.Dict;

public interface IDictService {

	void saveDict(Dict dict);

	Dict getDict(String code);

	List<Dict> findAll();

	void matchRejectLevel(String result, Map<String, Object> object);
	
	void refreshDict(String key) throws Exception;
	
}
