/**
 * xkaisun@gmail.com
 * Copyright (c) 2013-2018 All Rights Reserved.
 */

package com.qf.cobra.util;

import org.apache.commons.codec.binary.Base64;

/**
 * base64工具封装
 * @author XiaokaiSun
 * @version $Id: Base64Util.java, v 0.1 2018-02-01 16:25 XiaokaiSun Exp $$
 */
public class Base64Util {

    /**
     * base64编码
     * @param data
     * @return
     */
    public static String base64Encode(String data) {

        return Base64.encodeBase64String(data.getBytes());
    }

    /**
     * base64编码
     * @param bytes
     * @return
     */
    public static String base64Encode(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    /**
     * base64解码
     * @param data
     * @return
     */
    public static byte[] base64Decode(String data) {

        return Base64.decodeBase64(data.getBytes());
    }

    /**
     * base64解码
     * @param bytes
     * @return
     */
    public static String base64Decode2Str(byte[] bytes){
        return new String(Base64.decodeBase64(bytes));
    }
}