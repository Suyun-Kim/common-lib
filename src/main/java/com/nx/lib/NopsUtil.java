package com.nx.lib;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class NopsUtil {

    public static final String SERVER_FREEZING = "1";        // 프리징 서버
    public static final String SERVER_DEV      = "2";        // 개발서버
    public static final String SERVER_GRAPHIC  = "3";        // 그래픽서버
    public static final String SERVER_QA       = "18";       // QA 서버 (개발DB씀)
    public static final String SERVER_DAILYQA  = "22";       // DailyQA 서버
    public static final String SERVER_BALANCE  = "23";       // Balance 서버
    public static final String SERVER_COMPANY  = "28";       // 전사테스트서버

    public static String getIpAddress() {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

}
