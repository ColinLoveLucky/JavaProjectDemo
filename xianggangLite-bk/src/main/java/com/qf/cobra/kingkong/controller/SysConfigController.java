package com.qf.cobra.kingkong.controller;


import com.qf.cobra.kingkong.service.ISysConfigService;

import com.qf.cobra.loan.controller.DictController;
import com.qf.cobra.pojo.Dict;
import com.qf.cobra.pojo.NewPagination;
import com.qf.cobra.pojo.SysConfig;
import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/SysConfig")
public class SysConfigController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DictController.class);
    @Autowired
    private ISysConfigService sysConfigService;
    @ApiOperation(value="查询系统配置信息")
    @PostMapping("/")
    public ResponseData<NewPagination<Map<String, Object>>> query(@RequestBody NewPagination<Map<String, Object>> pagination){
        ResponseData<NewPagination<Map<String, Object>>> responseData = ResponseUtil.defaultResponse();
        try {
            sysConfigService.query(pagination);
            responseData.setData(pagination);
        } catch (Exception e) {
            LOGGER.error("系统配置信息查询结果失败", e);
            responseData.setCode(ResponseCode.SYSTEM_ERROR);
            responseData.setMsg("系统配置信息查询结果失败!");
        }
        return responseData;
    }
    @ApiOperation(value="保存系统配置信息")
    @PostMapping("/save")
    public ResponseData save(@RequestBody SysConfig  sysConfig){
        ResponseData<Dict> responseData = ResponseUtil.defaultResponse();
        try {
            sysConfigService.save(sysConfig);
        } catch (Exception e) {
            LOGGER.error("系统配置添加失败", e);
            responseData.setCode(ResponseCode.SYSTEM_ERROR);
            responseData.setMsg("系统配置添加失败");
        }
        return responseData;
    }
    @ApiOperation(value="修改系统配置信息")
    @PutMapping("/save")
    public ResponseData edit(@RequestBody SysConfig sysConfig){
        ResponseData<Dict> responseData = ResponseUtil.defaultResponse();
        try {
            sysConfigService.edit(sysConfig);
        } catch (Exception e) {
            LOGGER.error("修改系统配置信息失败", e);
            responseData.setCode(ResponseCode.SYSTEM_ERROR);
            responseData.setMsg("修改系统配置信息失败");
        }
        return responseData;
    }
    @ApiOperation(value="删除系统配置信息")
    @DeleteMapping("/{key}")
    public ResponseData remove(@PathVariable("key") String key){
        ResponseData<Dict> responseData = ResponseUtil.defaultResponse();
        try {
            sysConfigService.remove(key);
        } catch (Exception e) {
            LOGGER.error("删除系统配置信息失败", e);
            responseData.setCode(ResponseCode.SYSTEM_ERROR);
            responseData.setMsg("删除系统配置信息失败");
        }
        return responseData;
    }
    @ApiOperation(value="查询系统配置信息")
    @GetMapping("/{id}")
    public ResponseData findById(@PathVariable("id") String id){
        ResponseData<SysConfig> responseData = ResponseUtil.defaultResponse();
        try {
            SysConfig data= sysConfigService.findById(id);
            responseData.setData(data);
        } catch (Exception e) {
            LOGGER.error("查询系统配置信息失败", e);
            responseData.setCode(ResponseCode.SYSTEM_ERROR);
            responseData.setMsg("查询系统配置信息失败");
        }
        return responseData;
    }
}
