package com.quark.cobra.controller;

import com.quark.cobra.Utils.JsonUtil;
import com.quark.cobra.Utils.ResponseCode;
import com.quark.cobra.Utils.ResponseData;
import com.quark.cobra.Utils.ValidateUtil;
import com.quark.cobra.constant.CrmConstants;
import com.quark.cobra.domain.ForgotPasswordReqBo;
import com.quark.cobra.domain.ModifyPasswordReqBo;
import com.quark.cobra.domain.UserInfoReqBo;
import com.quark.cobra.domain.UserInfoResBo;
import com.quark.cobra.domain.RegistrationReqBo;
import com.quark.cobra.domain.RegistrationResBo;
import com.quark.cobra.entity.User;
import com.quark.cobra.exception.BusinessException;
import com.quark.cobra.service.ICheckService;
import com.quark.cobra.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Slf4j
@Api("User相关的action")
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private ICheckService checkService;

    @ApiOperation("用户注册")
    @PostMapping("/registration")
    public ResponseData createUser(@RequestBody RegistrationReqBo registrationReqBo) {
        checkService.checkRegistration(registrationReqBo);

        User user = userService.buildUser(registrationReqBo);
        userService.saveUser(user);

        RegistrationResBo registrationResBo = new RegistrationResBo(user.getId());
        return ResponseData.ok(registrationResBo);
    }

    @ApiOperation("忘记密码")
    @PostMapping("/forgotPwd")
    public ResponseData forgotPassword(@RequestBody @Valid ForgotPasswordReqBo forgotPasswordReqBo, BindingResult bindingResult) {
    	log.info("忘记密码请求参数:{}", JsonUtil.convert(forgotPasswordReqBo));
    	// 解析绑定参数校验结果
		String result = ValidateUtil.processBindResult(bindingResult);
		if(StringUtils.isNotBlank(result)) {
			log.warn("忘记密码-验证参数结果:{}", result);
			return ResponseData.error(ResponseCode.PARAM_DATA_INVALID, result);
		}
    	
        // 查询用户信息
    	List<User> userList = userService.findByMobile(forgotPasswordReqBo.getMobile());
    	if(CollectionUtils.isEmpty(userList)) {
    		log.warn("忘记密码-手机号:{},对应用户信息不存在!", forgotPasswordReqBo.getMobile());
    		throw new BusinessException(ResponseCode.PARAM_DATA_INVALID, "手机号对应用户信息不存在!");
    	}
        
        // 忘记密码
        userService.forgotPassword(userList.get(0), forgotPasswordReqBo);
        return ResponseData.ok();
    }

    @ApiOperation("修改密码")
    @PostMapping("/{userId}/modifyPwd")
    public ResponseData modifyPassword( @PathVariable("userId") String userId, @RequestBody @Valid ModifyPasswordReqBo modifyPasswordReqBo,
    		BindingResult bindingResult) {
    	log.info("用户ID:{},修改密码请求参数:{}", userId, JsonUtil.convert(modifyPasswordReqBo));
    	// 解析绑定参数校验结果
		String result = ValidateUtil.processBindResult(bindingResult);
		if(StringUtils.isNotBlank(result)) {
			log.warn("修改密码-验证参数结果:{}", result);
			return ResponseData.error(ResponseCode.PARAM_DATA_INVALID, result);
		}
		
    	// 验证用户信息是否存在
        User user = validateUser(userId);
        
        // 判断原密码是否正确
 		if(!userService.matches(modifyPasswordReqBo.getPassword(),user.getPassword())) {
 			log.error("原密码不正确!用户ID:{}", user.getId());
 			return ResponseData.error(ResponseCode.PARAM_DATA_INVALID, "原密码不正确");
 		}
        
        // 修改密码
        userService.modifyPassword(user, modifyPasswordReqBo);
        return ResponseData.ok();
    }
    
    /**
     * 用户基本信息录入
     * 
     * @param userInfoReqBo
     * @return
     */
    @ApiOperation("用户基本信息录入")
    @PostMapping(value = "/info")
    public ResponseData authRealName(@Valid @RequestBody UserInfoReqBo userInfoReqBo, BindingResult bindingResult) {
    	log.info("实名认证-请求参数:{}", JsonUtil.convert(userInfoReqBo));
    	
    	// 验证实名认证参数
    	String result = checkService.checkUserInfo(userInfoReqBo, bindingResult);
		if(StringUtils.isNotBlank(result)) {
			log.warn("实名认证-验证参数结果:{}", result);
			return ResponseData.error(ResponseCode.PARAM_DATA_INVALID, result);
		}
  	  
	  	// 验证用户信息是否存在
	  	User user = validateUser(userInfoReqBo.getUserId());
  	  
  	    // 判断身份证号是否已经存在
  	    List<User> userList = userService.findUserByIdNo(userInfoReqBo.getIdCard());
  	    if(CollectionUtils.isNotEmpty(userList)) {
  		    log.warn("该身份证号:{}已存在,!", userInfoReqBo.getIdCard());
  		    return ResponseData.error(ResponseCode.PARAM_DATA_INVALID, "该身份证号已存在,请勿重复认证!");
  	    }
  	
  	    // 判断是否已经实名通过,如果已实名通过,则直接返回实名通过信息
  	    if(null != user.getCertificationDate()) {
  	    	log.info("该用户:{}已实名认证过", userInfoReqBo.getUserId());
  		    return ResponseData.ok(new UserInfoResBo(CrmConstants.REALNAME_AUTH_SUCCESS_STATUS,
  				    CrmConstants.REALNAME_AUTH_SUCCESS_DESC));
  	    }
  	  
  	    // 实名认证
  	    UserInfoResBo userInfoResBo = userService.realNameAuth(userInfoReqBo);
  	    if(null == userInfoResBo) {
  		    return ResponseData.error(ResponseCode.REALNAME_AUTH_FAILED, "实名认证失败,请确认!");
  	    }
  	  
  	    return ResponseData.ok(userInfoResBo);
    }
    
    /**
     * 获取用户信息
     * 
     * @param userId
     * @return
     */
    @ApiOperation("获取用户信息")
    @GetMapping(value = "/{userId}")
    public ResponseData getUserInfo(@PathVariable("userId") String userId) {
    	// 验证用户信息是否存在
    	User user = validateUser(userId);
    	
    	// 将密码置空
    	user.setPassword(null);
    	return ResponseData.ok(user);
    }
    
    /**
     * 验证用户信息是否存在
     * 
     * @param userId 用户ID
     * @return
     */
    private User validateUser(String userId) {
    	// 查询用户信息
    	User user = userService.getUserById(userId);
    	if(null == user) {
    		log.warn("用户ID:{},对应用户信息不存在!", userId);
    		throw new BusinessException(ResponseCode.PARAM_DATA_INVALID, "用户ID无效!");
    	}
    	return user;
    }

	@ApiOperation("查询用户信息")
	@PostMapping(value = "/query")
    public ResponseData queryUser(@RequestBody Map<String,Object> params)
	{
		return	ResponseData.ok(userService.queryUser(params));
	}
}
