package com.qf.cobra.pojo;

public class SysConfig {
    private String id;
    private String key;
    private String value;
    private String notes;
    public String getId(){
        return this.id;
    }
    public void setId(String id){
        this.id=id;
    }
    public String getKey(){
        return this.key;
    }
    public void setKey(String key){
        this.key=key;
    }
    public String getValue(){
        return this.value;
    }
    public void setValue(String value){
        this.value=value;
    }
    public String getNotes(){
        return this.notes;
    }
    public void setNotes(String notes){
        this.notes=notes;
    }
}
