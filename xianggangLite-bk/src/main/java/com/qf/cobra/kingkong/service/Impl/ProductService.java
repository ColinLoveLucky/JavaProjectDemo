package com.qf.cobra.kingkong.service.Impl;
import com.qf.cobra.kingkong.service.IProductService;
import com.qf.cobra.pojo.NewPagination;
import com.qf.cobra.pojo.Pagination;
import com.qf.cobra.pojo.ProductConfiguration;
import com.qf.cobra.system.service.impl.DictServiceImpl;
import com.qf.cobra.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService implements IProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DictServiceImpl.class);
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public void saveProduct(ProductConfiguration product) {
       // product.setTimestamp(DateUtil.formatCurrentDateTime());
        mongoTemplate.save(product);
    }
    @Override
    public void query(NewPagination<Map<String,Object>> pagination) throws Exception {
        Query productsQuery = new Query();
        Map<String, Object> condition = pagination.getCondition();
        if (condition != null) {
            String name = (String) condition.get("productName");
            if (!StringUtils.isEmpty(name)) {
                productsQuery.addCriteria(Criteria.where("productName").is(name));
            }
        }
      //  productsQuery.with(new Sort(new Sort.Order(Sort.Direction.DESC, "timestamp")));
        // 分页前先查出满足条件的记录总数;
        int count = (int) mongoTemplate.count(productsQuery, ProductConfiguration.class);
        pagination.setPageTotal(count);
        // 数据库分页
        int size = pagination.getPageSize();
        int start = (pagination.getPageNo() - 1) * size;
        productsQuery.skip(start);
        productsQuery.limit(size);
        List<ProductConfiguration> products = mongoTemplate.find(productsQuery, ProductConfiguration.class);
        if (!org.springframework.util.CollectionUtils.isEmpty(products)) {
            List<Map<String, Object>> currentPageRecordList = new ArrayList<Map<String, Object>>();
            for (ProductConfiguration product : products) {
                Map<String, Object> record = new HashMap<String, Object>();
                record.put("productCategory", product.getProductCategory());
                record.put("productCategoryName",product.getProductCategoryName());
                record.put("productType",product.getProductType());
                record.put("productTypeName",product.getProductTypeName());
                record.put("productCode",product.getProductCode());
                record.put("productName",product.getProductName());
                record.put("configItems",product.getConfigItems());
                record.put("IsEnable",product.getIsEnabled());
                currentPageRecordList.add(record);
            }
            pagination.setData(currentPageRecordList);
        }
    }
}
