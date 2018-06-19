/**
 * @Title: [ResponseCode.java]
 * @Package: [com.quark.cobra.ndes.util]
 * @author: [ChangcaiCao]
 * @CreateDate: [2017年3月24日 下午1:25:51]   
 * @UpdateUser: [ChangcaiCao]
 * @UpdateDate: [2017年3月24日 下午1:25:51]   
 * @UpdateRemark: [说明本次修改内容]
 * @Description: [TODO(用一句话描述该文件做什么)]
 * @version: [V1.0]
 */
package com.quark.cobra.Utils;

/**
 * @ClassName: ResponseCode
 * @author: [ChangcaiCao]
 * @CreateDate: [2017年3月24日 下午1:25:51]
 * @UpdateUser: [ChangcaiCao]
 * @UpdateDate: [2017年3月24日 下午1:25:51]
 * @UpdateRemark: [说明本次修改内容]
 * @Description: [TODO(用一句话描述该文件做什么)]
 * @version: [V1.0]
 */
public class ResponseCode {

    // 成功
    public static Integer SUCCESS = 20000;

    // 回话失效
    public static Integer TOKEN_INVALID = 50403;

    // 系统异常
    public static Integer SYSTEM_ERROR = 50000;

    // 参数为空
    public static Integer PARAM_DATA_IS_NULL = 40000; 
    // 参数无效
    public static Integer PARAM_DATA_INVALID = 40001;
    // 实名认证失败
    public static Integer REALNAME_AUTH_FAILED = 40002; 

    // Policy服务不存在
    public static Integer POLICY_SERVICE_NOT_EXISTS = 40100;

    // NDES成功
    public static Integer NDES_SUCCESS = 2000;

}
