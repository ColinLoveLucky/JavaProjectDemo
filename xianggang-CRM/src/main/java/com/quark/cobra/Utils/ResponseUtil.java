/**
 * @Title: [ResponseUtil.java]
 * @Package: [com.qf.uca.util]
 * @author: [ChangcaiCao]
 * @CreateDate: [2016年7月14日 上午11:10:41]
 * @UpdateUser: [ChangcaiCao]
 * @UpdateDate: [2016年7月14日 上午11:10:41]
 * @UpdateRemark: [说明本次修改内容]
 * @Description: [TODO(用一句话描述该文件做什么)]
 * @version: [V1.0]
 */
package com.quark.cobra.Utils;


/**
 * @ClassName: ResponseUtil
 * @author: [ChangcaiCao]
 * @CreateDate: [2016年7月14日 上午11:10:41]
 * @UpdateUser: [ChangcaiCao]
 * @UpdateDate: [2016年7月14日 上午11:10:41]
 * @UpdateRemark: [说明本次修改内容]
 * @Description: [响应结果工具类]
 * @version: [V1.0]
 */
public class ResponseUtil {

    /*
     * 返回一个初始化的ResponseData对象
     */
    public static ResponseData defaultResponse() {
        return new ResponseData(ResponseCode.SUCCESS, "成功");
    }

}
