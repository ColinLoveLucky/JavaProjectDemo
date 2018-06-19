package com.qf.cobra.kingkong.controller;
import com.qf.cobra.kingkong.service.IUserService;
import com.qf.cobra.loan.controller.DictController;
import com.qf.cobra.pojo.NewPagination;
import com.qf.cobra.pojo.Pagination;
import com.qf.cobra.util.ResponseCode;
import com.qf.cobra.util.ResponseData;
import com.qf.cobra.util.ResponseUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/Users")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictController.class);
    @Autowired
    private IUserService userService;
    @ApiOperation(value="查询用户信息")
    @PostMapping("/")
    public ResponseData<NewPagination<Map<String, Object>>> query(@RequestBody NewPagination<Map<String, Object>> pagination){
        ResponseData<NewPagination<Map<String, Object>>> responseData = ResponseUtil.defaultResponse();
        try {
            userService.query(pagination);
            responseData.setData(pagination);
        } catch (Exception e) {
            LOGGER.error("用户信息查询结果失败", e);
            responseData.setCode(ResponseCode.SYSTEM_ERROR);
            responseData.setMsg("用户信息查询结果失败!");
        }
        return responseData;
    }
}
