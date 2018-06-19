package com.quark.cobra.controller;

import com.quark.cobra.Utils.ResponseData;
import com.quark.cobra.entity.SysConfig;
import com.quark.cobra.service.ISysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/SysConfig")
@Slf4j
@Api("SysConfig相关的Action")
public class SysConfigController {
    @Autowired
    private ISysConfigService sysConfigService;
    @ApiOperation("查询系统配置信息")
    @PostMapping(value = "/query")
    public ResponseData querySysConfig(@RequestBody Map<String,Object> params)
    {
        return	ResponseData.ok(sysConfigService.querySysConfig(params));
    }
    @ApiOperation("查询系统配置明细信息")
    @GetMapping(value = "/{id}")
    public ResponseData findsById(@PathVariable("id") String id){
        return	ResponseData.ok(sysConfigService.findById(id));
    }
    @ApiOperation("新增系统配置信息")
    @PostMapping(value = "/save")
    public ResponseData save(@RequestBody Map<String,Object> params){
            SysConfig sysConfig=new SysConfig();
            String key=  params.get("key")!=null?   params.get("key").toString():"";
            String value= params.get("value")!=null?   params.get("value").toString():"";
            String notes= params.get("notes")!=null?   params.get("notes").toString():"";
            sysConfig.setKey(key);
            sysConfig.setValue(value);
            sysConfig.setNotes(notes);
            sysConfigService.save(sysConfig);
            return ResponseData.ok();
    }
    @ApiOperation("更新系统配置信息")
    @PutMapping(value = "/save")
    public ResponseData update(@RequestBody  Map<String,Object> params){
        SysConfig sysConfig=new SysConfig();
        String id= params.get("id")!=null?   params.get("id").toString():"";
        String key=  params.get("key")!=null?   params.get("key").toString():"";
        String value= params.get("value")!=null?   params.get("value").toString():"";
        String notes= params.get("notes")!=null?   params.get("notes").toString():"";
        sysConfig.setId(id);
        sysConfig.setKey(key);
        sysConfig.setValue(value);
        sysConfig.setNotes(notes);
        sysConfigService.update(sysConfig);
        return ResponseData.ok();
    }
    @ApiOperation("删除系统配置信息")
    @DeleteMapping(value = "/{key}")
    public ResponseData remove(@PathVariable("key") String key){
        sysConfigService.remove(key);
        return ResponseData.ok();
    }
}
