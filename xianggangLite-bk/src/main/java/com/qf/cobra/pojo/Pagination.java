package com.qf.cobra.pojo;

import java.util.List;
import java.util.Map;

/**
 * 
* @Title: Pagination.java
* @Package com.qf.cobra.pojo
* @Description: 分页查询对象
* @author ZiyangTan  
* @date 2017年4月28日 上午11:59:29
* @version V1.0
 */
public class Pagination<T> {

	private int currentPage;
	private int pageSize;
	private int total;
	private List<T> data;
	private Map<String, Object> condition;
	
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public List<T> getData() {
		return data;
	}
	public void setData(List<T> data) {
		this.data = data;
	}
	public Map<String, Object> getCondition() {
		return condition;
	}
	public void setCondition(Map<String, Object> condition) {
		this.condition = condition;
	}
	
}
