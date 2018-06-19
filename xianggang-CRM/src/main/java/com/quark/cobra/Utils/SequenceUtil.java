/**
 * xkaisun@gmail.com
 * Copyright (c) 2013-2018 All Rights Reserved.
 */

package com.quark.cobra.Utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * sequence生成Uitl
 * @author XiaokaiSun
 * @version $Id: SequenceUtil.java, v 0.1 2018-02-02 11:04 XiaokaiSun Exp $$
 */
public class SequenceUtil {

    /** 随机数生成器 */
    public static Random random = new Random(1234567L);

    /** snowFlake 实例*/
    public static SnowFlake snowFlake = new SnowFlake(1, getUniqueId());

    /**
     * 获取ip的最后一位最为uniqId
     * @return
     */
    public static long getUniqueId() {
        try {
            String[] ip = InetAddress.getLocalHost().getHostAddress().split("\\.");

            String ipLast = ip[ip.length - 1];
            return Long.parseLong(ipLast);
        } catch (UnknownHostException e) {
            return random.nextInt(255);
        }
    }

    public static String getDateStr() {
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    /**
     * 生成sequence主方法
     * @return
     */
    public static String nextSequece() {
        String dateStr = getDateStr();

        return new StringBuilder(dateStr).append(snowFlake.nextId()).toString();
    }

    public static void main(String[] args) {
        System.out.println(SequenceUtil.nextSequece());
    }
}
