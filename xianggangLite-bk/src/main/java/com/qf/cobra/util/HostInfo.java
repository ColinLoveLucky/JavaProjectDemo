package com.qf.cobra.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Vector;

public class HostInfo {
    public static String hostinfo = "[can not resolve host info]";

    static {
        try {
            InetAddress address = InetAddress.getLocalHost();
            String hostname = address.getHostName();
            String ipaddress = address.getHostAddress();
            hostinfo = "[" + hostname + ":" + ipaddress + "]";
        } catch (UnknownHostException e) {
            e.printStackTrace();
            try {
                getLocalHostAddr();
            } catch (SocketException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void getLocalHostAddr() throws SocketException {
        Enumeration<NetworkInterface> allnetInterfaces;
        Vector<String> ipAddr = new Vector<String>();
        String ipLocalAddr = null;
        InetAddress ip = null;
        allnetInterfaces = NetworkInterface.getNetworkInterfaces();
        while (allnetInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = allnetInterfaces.nextElement();
            Enumeration<InetAddress> addresses = networkInterface
                    .getInetAddresses();
            while (addresses.hasMoreElements()) {
                ip = addresses.nextElement();
                ipAddr.add(ip.toString());
                if (null != ip && ip instanceof Inet4Address) {
                    String hostAddress = ip.getHostAddress();
                    if (!"127.0.0.1".equals(hostAddress)
                            && !"/127.0.0.1".equals(hostAddress)) {
                        ipLocalAddr = ip.toString().split("[/]")[1];
                        hostinfo = "[" + ip.getHostName() + ":" + ipLocalAddr
                                + "]";
                    }
                }
            }
        }
    }
}
