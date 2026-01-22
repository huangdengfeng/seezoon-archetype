package com.seezoon.infrastructure.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class NetUtils {

    public static final String COMMA = ",";


    public static String resolveMultiNicAddr(String nics) {
        if (nics == null) {
            return null;
        }
        return resolveMultiNicAddr(StringUtils.split(nics, COMMA));
    }

    public static String resolveMultiNicAddr(String[] nics) {
        for (String nic : nics) {
            String ip = resolveNicAddr(nic);
            if (ip != null) {
                log.debug("nics: {}, resolve nic : {}, ip: {}", nics, nic, ip);
                return ip;
            }
        }
        return null;
    }

    /**
     * 解析网卡IP
     *
     * @param nic 网卡标识，如eth1
     */
    public static String resolveNicAddr(String nic) {
        try {
            NetworkInterface ni = NetworkInterface.getByName(nic);
            Enumeration<InetAddress> addrs = ni.getInetAddresses();
            while (addrs.hasMoreElements()) {
                InetAddress i = addrs.nextElement();
                if (i instanceof Inet4Address) {
                    return i.getHostAddress();
                }
            }
            addrs = ni.getInetAddresses();
            return addrs.nextElement().getHostAddress();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取主机地址
     */
    public static String getHostIp() {
        try {
            InetAddress address = InetAddress.getLocalHost();

            // 如果是回环网卡地址, 则获取ipv4 地址
            if (address.isLoopbackAddress()) {
                address = getInet4Address();
            }
            if (address != null) {
                log.info("get host ip success, address:{}", address);
                return address.getHostAddress();
            }
        } catch (Exception e) {
            log.error("get host ip failure:", e);
        }

        return null;
    }

    /**
     * 获取IPV4网络配置
     */
    public static InetAddress getInet4Address() throws SocketException {
        // 获取所有网卡信息
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = networkInterfaces.nextElement();
            Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress ip = addresses.nextElement();
                if (ip instanceof Inet4Address) {
                    return ip;
                }
            }
        }
        return null;
    }
}
