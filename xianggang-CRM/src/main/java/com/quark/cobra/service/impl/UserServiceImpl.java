package com.quark.cobra.service.impl;

import com.quark.cobra.Utils.HttpEntityUtils;
import com.quark.cobra.Utils.JsonUtil;
import com.quark.cobra.Utils.RSAUtil;
import com.quark.cobra.constant.CrmConstants;
import com.quark.cobra.dao.repo.OauthClientDetailsRepo;
import com.quark.cobra.dao.repo.UserRepo;
import com.quark.cobra.domain.*;
import com.quark.cobra.entity.OauthClientDetails;
import com.quark.cobra.entity.User;
import com.quark.cobra.enums.SmsTypeEnums;
import com.quark.cobra.exception.BusinessException;
import com.quark.cobra.service.ISmsService;
import com.quark.cobra.service.IUserService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserRepo userRepo;

    @Value("${realname.auth.url}")
    private String realnameAuthUrl;
    
    @Resource(name="defaultRestTemplate")
    private RestTemplate restTemplate;
    
    @Autowired
    private ISmsService smsService;

    @Override
    public User buildUser(RegistrationReqBo registrationReqBo) {
        DateTime now = DateTime.now();
        User user = User.builder()
			.id(new ObjectId().toString())
            .mobile(registrationReqBo.getMobile())
            .password(encryptChange(registrationReqBo.getPassword()))
            .registrationDate(now.toDate())
            .updatePwdDate(now.toDate())
            .build();
        return user;
    }

    @Override
    public void saveUser(@NonNull User user) {
        userRepo.save(user);
    }

    @Override
    public List<User> findByMobile(String mobile) {
        List<User> userList = userRepo.findByMobile(mobile);
        return userList;
    }

	/**
     * 昵称默认生成规则:User+日期的年月日+手机末四位
     * @param mobile
     * @return
     */
    private String defaultNickName(String mobile) {
        String nickName = new StringBuilder()
            .append("User")
            .append(DateTime.now().toString("YYYYMMDD"))
            .append(StringUtils.trimToEmpty(mobile).substring(7))
            .toString();
        return nickName;
    }
    
    @Override
	public UserInfoResBo realNameAuth(UserInfoReqBo realNameAuthReqBo) {
		try {
			log.info("实名认证开始.userID:{}, realname:{},idCard:{}", realNameAuthReqBo.getUserId(),
					realNameAuthReqBo.getRealname(), realNameAuthReqBo.getIdCard());
			
			// 构建实名认证请求接口实体类
			RealNameAuthReq realNameAuthReq = new RealNameAuthReq(CrmConstants.QUERY_SYSTEM, CrmConstants.QUERY_USER,
					realNameAuthReqBo.getRealname(), realNameAuthReqBo.getIdCard());
			
			// 调用实名认证接口
			DacRealNameAuthResult realNameAuthResult = restTemplate.postForObject(realnameAuthUrl, HttpEntityUtils.getHttpEntityForPost(JsonUtil.convert(realNameAuthReq)),
					DacRealNameAuthResult.class);
			log.info("调用实名认证返回结果:{}", JsonUtil.convert(realNameAuthResult));
			if(realNameAuthResult.isSuccess()) {
				String data = JsonUtil.convert(realNameAuthResult.getData());
				if(StringUtils.isBlank(data)) {
					log.error("调用实名认证没有返回认证结果!");
					return null;
				}
				
				RealNameAuthRes realNameAuthRes = JsonUtil.convert(data, RealNameAuthRes.class);
				// 判断是否认证成功
				if(CrmConstants.REALNAME_AUTH_SUCCESS_STATUS.equals(realNameAuthRes.getQueryStatus())) {
					User updateUser = new User();
					updateUser.setId(realNameAuthReqBo.getUserId());
					updateUser.setIdName(realNameAuthReqBo.getRealname());
					updateUser.setIdNo(realNameAuthReqBo.getIdCard());
					updateUser.setSex(realNameAuthReqBo.getSex().name());
					updateUser.setAge(realNameAuthReqBo.getAge());
					updateUser.setMaritalStatus(realNameAuthReqBo.getMaritalStatus().name());
					updateUser.setEducationLevel(realNameAuthReqBo.getEducationLevel().name());
					updateUser.setCertificationDate(new Date());
					
					// 更新用户信息
					int count = userRepo.updateUserById(updateUser);
					if(count < 1) {
						String errorMsg = "更新用户信息失败!";
						log.error(errorMsg);
						throw new BusinessException(errorMsg);
					}
				}
				return new UserInfoResBo(realNameAuthRes.getQueryStatus(), realNameAuthRes.getDescription());
			}
			return null;
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			log.error("实名认证发生异常!realname:{},idCard:{}", realNameAuthReqBo.getRealname(), realNameAuthReqBo.getIdCard(), e);
			throw new BusinessException("实名认证发生异常!");
		}
	}

	@Override
	public User getUserById(String userId) {
		return userRepo.findById(userId);
	}

	@Override
	public void forgotPassword(User user, ForgotPasswordReqBo forgotPasswordReqBo) {
		
		// 验证手机验证码
		smsService.checkCaptcha(user.getMobile(),forgotPasswordReqBo.getCaptcha(),SmsTypeEnums.FORGOT_PASSWORD);
		
		User updateUser = new User();
		updateUser.setId(user.getId());
		updateUser.setPassword(encryptChange(forgotPasswordReqBo.getPassword()));
		
		// 更新用户密码
		int updateCount = userRepo.updateUserById(updateUser);
		if(updateCount < 1) {
			log.error("更新用户密码失败!用户ID:{}", user.getId());
			throw new BusinessException("更新用户密码失败!");
		}
	}

	@Override
	public void modifyPassword(User user, ModifyPasswordReqBo modifyPasswordReqBo) {
		
		User updateUser = new User();
		updateUser.setId(user.getId());
		updateUser.setPassword(encryptChange(modifyPasswordReqBo.getNewPassword()));
		
		// 更新用户密码
		int updateCount = userRepo.updateUserById(updateUser);
		if(updateCount < 1) {
			log.error("更新用户密码失败!用户ID:{}", user.getId());
			throw new BusinessException("更新用户密码失败!");
		}
		
	}

	@Override
	public List<User> findUserByIdNo(String idNo) {
		return userRepo.findByIdNo(idNo);
	}

	@Value("${privatekey}")
	private String privateKey;
    @Autowired
	private PasswordEncoder passwordEncoder;
	@Override
	public boolean matches(String cipherPassword, String cryptoPassword) {
		String clearPassword = null;
		try {
			cipherPassword = cipherPassword
				.replaceAll("\\.","+")
				.replaceAll("\\_","/")
				.replaceAll("\\-","=");
			clearPassword = RSAUtil.decrypt(cipherPassword,privateKey,RSAUtil.ALGO_NAME,true);
		} catch (Exception e) {
			Assert.state(false,"密码解码失败");
		}
		return passwordEncoder.matches(clearPassword,cryptoPassword);
	}

	@Override
	public String encryptChange(String cipherPassword) {
		String clearPassword = null;
		try {
			cipherPassword = cipherPassword
				.replaceAll("\\.","+")
				.replaceAll("\\_","/")
				.replaceAll("\\-","=");
			clearPassword = RSAUtil.decrypt(cipherPassword,privateKey,RSAUtil.ALGO_NAME,true);
		} catch (Exception e) {
			Assert.state(false,"密码解码失败");
		}
		return passwordEncoder.encode(clearPassword);
	}

	@Autowired
	private OauthClientDetailsRepo oauthClientDetailsRepo;
	@Override
	public OauthClientDetails findOauthClientDetailsByClientId(String clientId) {
		return oauthClientDetailsRepo.findByClientId(clientId);
	}

	@Override
	public Map<String,Object> queryUser(Map<String,Object> params) {
		return userRepo.queryUser(params);
	}
}
