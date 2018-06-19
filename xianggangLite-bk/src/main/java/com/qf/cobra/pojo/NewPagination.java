package com.qf.cobra.pojo;

import java.util.List;
import java.util.Map;

public class NewPagination<T> {

    private int pageNo;
    private int pageSize;
    private int pageTotal;
    private List<T> data;
    private Map<String, Object> condition;

    public int getPageNo(){
        return pageNo;
    }
    public void setPageNo(int pageNo){
        this.pageNo = pageNo;
    }
    public int getPageSize() {
        return pageSize;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    public int getPageTotal() {
        return pageTotal;
    }
    public void setPageTotal(int total) {
        this.pageTotal = total;
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

