package com.quark.cobra.service.impl;

import com.quark.cobra.Utils.HttpClientUtil;
import com.quark.cobra.Utils.RedisUtil;
import com.quark.cobra.Utils.SequenceUtil;
import com.quark.cobra.Utils.SnowFlake;
import com.quark.cobra.config.properties.SmsProperties;
import com.quark.cobra.constant.CrmConstants;
import com.quark.cobra.dao.repo.SysConfigRepo;
import com.quark.cobra.enums.SmsTypeEnums;
import com.quark.cobra.service.ISmsService;

import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import reactor.core.support.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SmsServiceImpl implements ISmsService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private SmsProperties smsProperties;
    @Autowired
    private SysConfigRepo sysConfigRepo;
    
    @Autowired
	private FreeMarkerConfig freeMarkerConfig;


    @Override
    public String findSysConfigValue(String key) {
        return findSysConfigValue(key,"");
    }

    @Override
    public String findSysConfigValue(String key,String defaultValue) {
        String value_default = StringUtils.isEmpty(defaultValue)?"":defaultValue;
        String value = sysConfigRepo.findByKey(key);
        return StringUtils.isEmpty(value)?value_default:value;
    }

    @Override
    public void sendMessage(String mobile, SmsTypeEnums smsType) {
        String captcha = createCaptcha();
        log.info("验证码:{}", captcha);
        String message = buildCaptchaMessage(captcha, smsType);
        sendMessage(mobile, message, smsType.getLabel());
        cacheCaptcha(smsType.getCacheKeyPrefix(), mobile, captcha);
    }

    @Override
    public void sendMessage(String mobile, String message, String besId) {
        String[] result = sendWithEmay(message, new String[]{mobile}, besId);
        Assert.isTrue(StringUtils.equals("0", result[0]), result[1]);
    }

    public void checkCaptcha(String prefix, String mobile, String captcha) {
        String captchaCache = getCaptchaFromCache(prefix, mobile);
        Assert.isTrue(StringUtils.equalsIgnoreCase(captchaCache, captcha), "验证码输入不正确");
        cleanCacheCaptcha(prefix, mobile);
    }

    @Override
    public void checkCaptcha(String mobile, String captcha, SmsTypeEnums smsType) {
        checkCaptcha(smsType.getCacheKeyPrefix(), mobile, captcha);
    }
    
    @Override
    public boolean needImageCaptcha(String mobile) {
        Long count = statisticsSendSmsCount(mobile);
        Long maxSend = Long.valueOf(findSysConfigValue(CrmConstants.NEED_IMAGE_CAPTCHA_COUNT,"0"));
        return count >= maxSend;
    }

    @Override
    public void increaseSendSmsCount(String key){
        Long expireTime = Long.valueOf(findSysConfigValue(CrmConstants.COUNT_MAX_TIMEOUT,"60"));
        String random = String.valueOf(SequenceUtil.nextSequece());
        String patten = new StringBuffer()
            .append(CrmConstants.CRM_REDIS_KEY_PREFIX)
            .append(CrmConstants.SMS_DIR_REDIS_KEY)
            .append(key)
            .append(":")
            .append(random)
            .toString();
        stringRedisTemplate.opsForValue().set(patten,random);
        stringRedisTemplate.expire(patten, expireTime, TimeUnit.MINUTES);
    }

    @Override
    public long statisticsSendSmsCount(String key) {
        String patten = new StringBuffer()
            .append(CrmConstants.CRM_REDIS_KEY_PREFIX)
            .append(CrmConstants.SMS_DIR_REDIS_KEY)
            .append(key)
            .append("*").toString();
        Set<String> keys = stringRedisTemplate.keys(patten);
        return keys.size();
    }

    /**
     * 缓存手机验证码
     * @param prefix
     * @param mobile
     * @param captcha
     */
    private void cacheCaptcha(String prefix, String mobile, String captcha) {
        Long expireTime = Long.valueOf(findSysConfigValue(CrmConstants.CAPTCHA_MAX_TIMEOUT,"30"));
        String captchaKey = new StringBuffer()
            .append(CrmConstants.CRM_REDIS_KEY_PREFIX)
            .append(CrmConstants.SMS_DIR_REDIS_KEY)
            .append(prefix)
            .append(":")
            .append(mobile)
            .toString();
        stringRedisTemplate.opsForValue().set(captchaKey,captcha);
        stringRedisTemplate.expire(captchaKey, expireTime, TimeUnit.MINUTES);
    }

    /**
     *
     * @param prefix 短信类型
     * @param mobile 手机号
     */
    private void cleanCacheCaptcha(String prefix, String mobile) {
        String captchaKey = new StringBuffer()
            .append(CrmConstants.CRM_REDIS_KEY_PREFIX)
            .append(CrmConstants.SMS_DIR_REDIS_KEY)
            .append(prefix)
            .append(":")
            .append(mobile)
            .toString();
        stringRedisTemplate.delete(captchaKey);
    }

    /**
     * 从缓存中获取短信验证码
     * @param prefix 短信类型
     * @param mobile 手机号
     */
    private String getCaptchaFromCache(String prefix, String mobile) {
        String captchaKey = new StringBuffer()
            .append(CrmConstants.CRM_REDIS_KEY_PREFIX)
            .append(CrmConstants.SMS_DIR_REDIS_KEY)
            .append(prefix)
            .append(":")
            .append(mobile)
            .toString();
        return stringRedisTemplate.opsForValue().get(captchaKey);
    }


    private String buildCaptchaMessage(String captcha, SmsTypeEnums smsType) {
    	try {
    		// 替换FreeMarker文件中的参数Map
    		Map<String, Object> map = new HashMap<String, Object>(4);
    		map.put("captcha", captcha);
    		
    		// 取得FreeMarker模板,并将动态参数合并返回短信内容
    		Template template = freeMarkerConfig.getConfiguration().getTemplate(smsType.getSmsTemplatePath());
    		String smsMsg = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            return smsMsg;
		} catch (Exception e) {
			log.error("获取短信模板发生异常!", e);
			return null;
		}
    	
    }

    /**
     * 创建随机验证码
     * @return
     */
    private String createCaptcha() {
        return String.valueOf(RandomUtils.nextInt(100000, 999999));
    }

    /**
     * 短信发送(亿美软通)
     *
     * @param message
     * @param mobile
     * @return .[0]:0-success 1-fail .[1]:resultMessage
     */
    private String[] sendWithEmay(String message, String[] mobile, String besId) {
        String[] result = new String[2];
        result[0] = "0";
        result[1] = "短信发送成功。";

        String mobiles = ArrayUtils.toString(mobile);
        mobiles = mobiles.substring(1, mobiles.length() - 1);

        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("phoneNumber", mobiles));
        formparams.add(new BasicNameValuePair("deptType", smsProperties.getDeptType()));
        formparams.add(new BasicNameValuePair("besType", smsProperties.getBesType()));
        formparams.add(new BasicNameValuePair("thirdType", smsProperties.getThirdType()));
        formparams.add(new BasicNameValuePair("besId", besId));
        formparams.add(new BasicNameValuePair("content", message));

        String resultStr = null;
        try {
            log.info("短信平台地址：{}, 接口调用参数：{}", smsProperties.getUrl(), formparams);
            resultStr = HttpClientUtil.doPost(smsProperties.getUrl(), formparams, "UTF-8", null);
            resultStr = StringUtils.trimToEmpty(resultStr);
            if (!resultStr.contains("OK")) {
                result[0] = "1";
                result[1] = resultStr;
            }
        } catch (Exception e) {
            result[0] = "1";
            result[1] = "发送短信失败:" + e.getMessage();
        }

        return result;
    }
}
