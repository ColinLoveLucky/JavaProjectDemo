package com.qf.cobra.kingkong.service;

import com.qf.cobra.pojo.NewPagination;
import com.qf.cobra.pojo.ProductConfiguration;
import java.util.Map;

public interface IProductService {
    void saveProduct(ProductConfiguration product);

   void query(NewPagination<Map<String, Object>> pagination)  throws Exception;;
}
