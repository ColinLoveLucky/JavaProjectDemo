/**
 * xkaisun@gmail.com
 * Copyright (c) 2013-2018 All Rights Reserved.
 */

package com.qf.cobra.util;

/**
 * 消息编码
 * @author XiaokaiSun
 * @version $Id: MsgEncodeUtil.java, v 0.1 2018-02-01 16:14 XiaokaiSun Exp $$
 */
public class MsgEncodeUtil {

    /**
     * 消息压缩
     * @param msg
     * @return
     */
    public static String msgEncode(String msg) {

        if (msg == null || msg.length() == 0) {
            return msg;
        }

        byte[] compressStr = GzipUtil.compress(msg);

        String rtn = Base64Util.base64Encode(compressStr);

        return rtn;
    }

    /**
     * 消息解压
     * @param encodeMsg
     * @return
     */
    public static String msgDecode(String encodeMsg) {

        if (encodeMsg == null || encodeMsg.length() == 0) {
            return encodeMsg;
        }

        byte[] compressBytes = Base64Util.base64Decode(encodeMsg);

        byte[] unCompressStr = GzipUtil.uncompress(compressBytes);

        String rtn = new String(unCompressStr);

        return rtn;
    }





    public static void main(String[] args) {
        String test = "{\"widget\": {\n"
                      + "    \"debug\": \"on\",\n"
                      + "    \"window\": {\n"
                      + "        \"title\": \"Sample Konfabulator Widget\",\n"
                      + "        \"name\": \"main_window\",\n"
                      + "        \"width\": 500,\n"
                      + "        \"height\": 500\n"
                      + "    },\n"
                      + "    \"image\": { \n"
                      + "        \"src\": \"Images/Sun.png\",\n"
                      + "        \"name\": \"sun1\",\n"
                      + "        \"hOffset\": 250,\n"
                      + "        \"vOffset\": 250,\n"
                      + "        \"alignment\": \"center\"\n"
                      + "    },\n"
                      + "    \"text\": {\n"
                      + "        \"data\": \"Click Here\",\n"
                      + "        \"size\": 36,\n"
                      + "        \"style\": \"bold\",\n"
                      + "        \"name\": \"外伤休克发号\",\n"
                      + "        \"hOffset\": 250,\n"
                      + "        \"vOffset\": 100,\n"
                      + "        \"alignment\": \"center\",\n"
                      + "        \"onMouseUp\": \"顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙顺孙.opacity = (sun1.opacity / 100) * 90;\"\n"
                      + "    }\n" + "}}    ";

        String encodeMsg = MsgEncodeUtil.msgEncode(test);

        System.out.println("原始长度：" + test.length());
        System.out.println("压缩长度：" + encodeMsg.length());

        System.out.println(encodeMsg);

        String decodeMsg = MsgEncodeUtil.msgDecode(encodeMsg);

        System.out.println(test.equals(decodeMsg));

    }

}