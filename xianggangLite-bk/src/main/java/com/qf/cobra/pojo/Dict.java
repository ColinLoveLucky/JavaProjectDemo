package com.qf.cobra.pojo;

import java.util.List;

/**
 * 
* @Title: Dict.java
* @Package com.quark.cobra.bizapp.pojo
* @Description: 系统字典类
* @author ZiyangTan  
* @date 2017年4月27日 下午9:15:32
* @version V1.0
 */
public class Dict {

	private String label;
	private Object value;
	private String remark;
	private Boolean disabled;
	private List<Dict> children;
	
	public Dict() {
	}

	public Dict(String label, Object value) {
		this.label = label;
		this.value = value;
	}
	
	public Dict(String label, Object value, String remark, Boolean disabled) {
		super();
		this.label = label;
		this.value = value;
		this.remark = remark;
		this.disabled = disabled;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public List<Dict> getChildren() {
		return children;
	}
	public void setChildren(List<Dict> children) {
		this.children = children;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}
}
