package com.qf.cobra.kingkong.controller;

import com.qf.cobra.kingkong.service.IProductService;
import com.qf.cobra.loan.controller.DictController;
import com.qf.cobra.pojo.*;
import com.qf.cobra.system.service.IDictService;
import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DictController.class);
    @Autowired
    private IProductService productService;
    @ApiOperation(value="保存产品信息")
    @PostMapping("/save")
    public ResponseData save(@RequestBody ProductConfiguration product){
        ResponseData<Dict> responseData = ResponseUtil.defaultResponse();
//        try {
//            productService.saveProduct(product);
//        } catch (Exception e) {
//            LOGGER.error("产品添加失败", e);
//            responseData.setCode(ResponseCode.SYSTEM_ERROR);
//            responseData.setMsg("产品添加失败");
//        }
        return responseData;
    }
    @ApiOperation(value="查询产品信息")
    @PostMapping("/")
    public ResponseData<NewPagination<Map<String, Object>>> query(@RequestBody NewPagination<Map<String, Object>> pagination){
        ResponseData<NewPagination<Map<String, Object>>> responseData = ResponseUtil.defaultResponse();
        try {
            productService.query(pagination);
            responseData.setData(pagination);
        } catch (Exception e) {
            LOGGER.error("产品信息查询结果失败", e);
            responseData.setCode(ResponseCode.SYSTEM_ERROR);
            responseData.setMsg("产品信息查询结果失败!");
        }
        return responseData;
    }
}
