package com.qf.cobra.pojo;

public class ProductConfiguration {
    private String productCategory;
    private boolean isEnabled;
    private String productCategoryName;
    private String productType;
    private String productTypeName;
    private String productCode;
    private String productName;
    private String configItems;
    public void setProdutCategory(String produtCategory){
        this.productCategory=produtCategory;
    }
    public String getProductCategory(){
        return this.productCategory;
    }
    public boolean getIsEnabled() {
        return isEnabled;
    }
    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
    public void setProductCategoryName(String productCategoryName) {
        this.productCategoryName = productCategoryName;
    }
    public String getProductCategoryName() {
        return this.productCategoryName;
    }
    public void setProductType(String productType) {
        this.productType = productType;
    }
    public String getProductType() {
        return this.productType;
    }
    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }
    public String getProductTypeName(){
        return productTypeName;
    }
    public void setProductCode(String productCode){
        this.productCode=productCode;
    }
    public String getProductCode(){
        return productCode;
    }
    public void setProductName(String productName){
        this.productName=productName;
    }
    public String getProductName(){
        return this.productName;
    }
    public void setConfigItems(String configItems){
        this.configItems=configItems;
    }
    public String getConfigItems(){
        return  this.configItems;
    }
}
