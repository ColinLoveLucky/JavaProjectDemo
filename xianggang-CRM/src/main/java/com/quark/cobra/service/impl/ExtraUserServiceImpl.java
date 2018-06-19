package com.quark.cobra.service.impl;

import com.quark.cobra.Utils.JsonUtil;
import com.quark.cobra.dao.repo.PendingUserRepo;
import com.quark.cobra.dao.repo.UserRepo;
import com.quark.cobra.domain.RegistrationReceptionReqBo;
import com.quark.cobra.entity.PendingUser;
import com.quark.cobra.entity.User;
import com.quark.cobra.service.IExtraUserService;
import com.quark.cobra.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.support.Assert;

import java.util.List;

@Slf4j
@Service
public class ExtraUserServiceImpl implements IExtraUserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PendingUserRepo pendingUserRepo;

    @Autowired
    private IUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String userReception(RegistrationReceptionReqBo receptionUser) {
        String mobile = receptionUser.getMobile();
        String productId = receptionUser.getProductId();
        List<User> userList = userService.findByMobile(mobile);
        String userId = null;
        DateTime now = DateTime.now();

        Assert.state(userList.size() <= 1,"内部数据有误,请联系管理员");

        if(CollectionUtils.isEmpty(userList)){
            log.info("产品{}-手机号{}未被注册,数据允许写入.",productId,mobile);
            userId = new ObjectId().toString();
            String clearPassword = mobile.replaceAll("(\\d{5})(\\d{6})","$2");
            userRepo.save(User.builder()
                .id(userId)
                .mobile(receptionUser.getMobile())
                .password(passwordEncoder.encode(clearPassword))
                .tenant(receptionUser.getClientId())
                .idNo(receptionUser.getIdCard())
                .idName(receptionUser.getRealname())
                .registrationDate(now.toDate())
                .age(receptionUser.getAge())
                .sex(receptionUser.getSex())
                .maritalStatus(receptionUser.getMaritalStatus())
                .educationLevel(receptionUser.getEducationLevel())
                .registrationDate(now.toDate())
                .updatePwdDate(now.toDate())
                .registrationDate(now.toDate())
                .build());
        }else if (userList.size() == 1){
            User mobileUser = userList.get(0);
            userId = mobileUser.getId();
            if(StringUtils.isEmpty(mobileUser.getIdNo())){
                log.info("产品{}-手机号{}已被注册,但尚未被实名,数据允许写入",productId,mobile);
                userRepo.updateUserById(User.builder()
                    .id(userId)
                    .idNo(receptionUser.getIdCard())
                    .idName(receptionUser.getRealname())
                    .registrationDate(now.toDate())
                    .age(receptionUser.getAge())
                    .sex(receptionUser.getSex())
                    .maritalStatus(receptionUser.getMaritalStatus())
                    .educationLevel(receptionUser.getEducationLevel())
                    .build());
            } else {
                log.info("产品{}-手机号{}已被注册,且被实名,数据无需写入",productId,mobile);
                if(!StringUtils.equalsIgnoreCase(mobileUser.getIdNo(),mobileUser.getIdNo())
                    || !StringUtils.equalsIgnoreCase(mobileUser.getIdName(),mobileUser.getIdName())){
                    pendingUserRepo.save(PendingUser.builder()
                        .data(JsonUtil.convert(receptionUser))
                        .conflictingUserId(userId)
                        .conflictNotes(String.format("身份证与姓名不一致,id=%s,idNo=%s,idName=%s",userId,mobileUser.getIdNo(),mobileUser.getIdName()))
                        .createDate(now.toDate())
                        .build());
                    Assert.state(false,"拒绝录入,请联系管理员");
                }
            }
        }
        return userId;
    }
}
