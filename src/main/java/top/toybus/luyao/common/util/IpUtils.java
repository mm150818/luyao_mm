package top.toybus.luyao.common.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

/**
 * 与IP地址相关的工具类
 * 
 * @author SUNXG
 *
 */
public class IpUtils {
    /**
     * 获取请求客户端的IP地址
     * 
     * @param request
     * @return IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length() = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        // System.out.println("IP:" + ipAddress.toString());
        return ipAddress;
    }

    /**
     * 将字符串IP地址转为长整形IP
     * <p>
     * 如：192.168.0.254 -> 3232235774
     * 
     * @param strIp
     * @return
     */
    public static long ip2Long(String strIp) {
        long ipNum = 0;
        try {
            if (strIp != null) {
                String ips[] = strIp.split("\\.");
                for (int i = 0; i < ips.length; i++) {
                    int k = Integer.parseInt(ips[i]);
                    ipNum = ipNum + k * (1L << ((3 - i) * 8));
                }
            }
        } catch (Exception e) {
        }
        return ipNum;
    }

    /**
     * 将长整形IP地址转为字符串IP
     * <p>
     * 232235774L -> 192.168.0.254
     * 
     * @param longIp
     * @return
     */
    public static String long2Ip(long longIp) {
        StringBuffer sb = new StringBuffer("");
        // 直接右移24位
        sb.append(String.valueOf((longIp >>> 24)));
        sb.append(".");
        // 将高8位置0，然后右移16位
        sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
        sb.append(".");
        // 将高16位置0，然后右移8位
        sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
        sb.append(".");
        // 将高24位置0
        sb.append(String.valueOf((longIp & 0x000000FF)));
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(ip2Long("127.0.0.1"));
    }

}
