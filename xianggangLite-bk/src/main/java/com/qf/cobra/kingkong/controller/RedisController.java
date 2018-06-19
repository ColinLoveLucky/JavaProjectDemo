package com.qf.cobra.kingkong.controller;

import com.qf.cobra.kingkong.service.Impl.RedisService;
import com.qf.cobra.loan.controller.DictController;
import com.qf.cobra.pojo.NewPagination;
import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Redis")
public class RedisController {
    @Autowired
    private RedisService redisService;
    private static final Logger LOGGER = LoggerFactory.getLogger(DictController.class);
    @ApiOperation(value="查询Redis信息")
    @PostMapping("/")
    public ResponseData<NewPagination<Map<String, Object>>> query(@RequestBody NewPagination<Map<String, Object>> pagination){
        ResponseData< NewPagination<Map<String, Object>>> responseData = ResponseUtil.defaultResponse();
        try {
             redisService.query(pagination);
            responseData.setData(pagination);
        } catch (Exception e) {
            LOGGER.error("Redis查询结果失败", e);
            responseData.setCode(ResponseCode.SYSTEM_ERROR);
            responseData.setMsg("Redis查询结果失败!");
        }
        return responseData;
    }
    @ApiOperation(value="删除Redis信息")
    @DeleteMapping("/{key}")
    public ResponseData delete(@PathVariable String key){
        ResponseData responseData = ResponseUtil.defaultResponse();
        try {
            redisService.delete(key);
        } catch (Exception e) {
            LOGGER.error("删除Redis信息失败", e);
            responseData.setCode(ResponseCode.SYSTEM_ERROR);
            responseData.setMsg("删除Redis信息失败!");
        }
        return responseData;
    }
    @ApiOperation(value="添加Redis信息")
    @PostMapping("/save")
    public ResponseData save(String key,String value){
        ResponseData responseData = ResponseUtil.defaultResponse();
        try {
            redisService.save(key,value);
        } catch (Exception e) {
            LOGGER.error("添加Redis信息失败", e);
            responseData.setCode(ResponseCode.SYSTEM_ERROR);
            responseData.setMsg("添加Redis信息失败!");
        }
        return responseData;
    }
}
