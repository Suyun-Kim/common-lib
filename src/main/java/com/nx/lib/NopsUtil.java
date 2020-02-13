package com.nx.lib;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class NopsUtil {

    public static final String SERVER_FREEZING = "1";        // 프리징 서버
    public static final String SERVER_DEV           = "2";        // 개발서버
    public static final String SERVER_GRAPHIC       = "3";        // 그래픽서버
    public static final String SERVER_DESIGN_TEST01 = "4";        // 기획테스트서버01
    public static final String SERVER_DESIGN_TEST02 = "5";        // 기획테스트서버02
    public static final String SERVER_DESIGN_TEST03 = "6";        // 기획테스트서버03
    public static final String SERVER_DESIGN_TEST04 = "7";        // 기획테스트서버04
    public static final String SERVER_DESIGN_TEST05 = "8";        // 기획테스트서버05
    public static final String SERVER_QA            = "18";       // QA 서버 (개발DB씀)
    public static final String SERVER_DAILYQA       = "22";       // DailyQA 서버
    public static final String SERVER_BALANCE       = "23";       // Balance 서버
    public static final String SERVER_COMPANY       = "28";       // 전사테스트서버
    public static final String SERVER_FREEZINGFIX  = "35";       // 전사테스트서버

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

    /**
     * HttpServletRequest 에서 사용자 정보 가져오기
     * @return
     */
    public static String getUser() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getRemoteUser();
    }

    /**
     *
     * @param keyList
     * @param column
     * @return
     */
    public static int columnCheck(List<String> keyList, String column) {

        int chk = 0;
        for(String columnKey : keyList) {
            if (columnKey.equalsIgnoreCase(column)) {
                chk++;
                break;
            }
        }

        return chk;
    }

    /**
     *
     * @param str
     * @return
     */
    public static String removeSpecialChar(String str) {
        return str.replaceAll("[^a-zA-Z]", "");
    }

    /**
     * 입력으로 받은 테이블을 읽어서 CSV 형식으로 변환한다.
     *
     * @param list
     * @return
     */
    public static String makeCSV(List<Map<String, Object>> list) {
        try {

            if (list.size() > 0) {
                StringBuilder str = new StringBuilder();

                int i = 0;

                for (Map<String, Object> map : list) {

                    if (i == 0) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            str.append(entry.getKey()).append(",");
                        }

                        if (map.size() > 0) {
                            str.deleteCharAt(str.lastIndexOf(",")).append("\n");
                        }
                    }

                    for (Map.Entry<String, Object> entry : map.entrySet()) {

                        String value = String.valueOf(entry.getValue());

                        // 소수점 뒷자리가 .0일경우 제거
                        if( isNumber(value) ) {
                            double doubleVal = Double.parseDouble(value);
                            double result = doubleVal - (int)doubleVal;
                            if(result == 0) {
                                value = (int)doubleVal + "";
                            }
                        }

                        if (value.contains(",")) {
                            // 콤마가 내용안에 포함되어있고 쌍따옴표가 가 내용안에 포함되어 있으면 쌍따옴표를 2개로 바꿔줌.
                            value.replaceAll("\"", "\"\"");
                            //콤마가 중간에 있으면 " 으로 감싸줌
                            str.append("\"").append(value).append("\"");
                        } else {
                            str.append(value);
                        }
                        str.append(",");
                    }

                    if (map.size() > 0) {
                        str.deleteCharAt(str.lastIndexOf(",")).append("\n");
                    }

                    i++;
                }

                return str.deleteCharAt(str.lastIndexOf("\n")).toString();
            } else
                return "";
        } catch (Exception ex) {
            return "";
        }
    }

    public static boolean isNumber(String value) {

        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }

    }

    public static boolean sendNotificationChannel(String content, String channel) {
        String recipients = "\"#" + channel + "\"";
        return sendNotifications(content, "","black",recipients);
    }

    public static boolean sendNotificationUser(String content, String user) {
        String recipients = "\"@" + user + "\"";
        return sendNotifications(content, "","black",recipients);
    }

    private static boolean sendNotifications(String content, String attachments, String color, String recipients) {

        try {
            // noti API 호출할 url 생성
            final String notiUrl = "http://noti.npixel.co.kr/api/notification/v1.0/send";

            HttpHeaders headers = new HttpHeaders();
            Charset utf8 = Charset.forName("UTF-8");
            MediaType mediaType = new MediaType("application", "json", utf8);
            headers.setContentType(mediaType);

            String requestJson =    "{" +
                    "\"data\": [{" +
                    "\"send_type\": \"5\"," +
                    "\"recipients\": ["+recipients+"]," +
                    "\"bot_name\": \"NOPS Noti Service\"," +
                    "\"content\": \"" + content + "\"," +
                    "\"attachments\":[{" +
                    "\"text\":\"" + attachments + "\"," +
                    "\"color\": \"" + color + "\"" +
                    "}]" +
                    "}]" +
                    "}";

            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

            // API 호출을 위한 RestTemplate 인스턴스 생성
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForObject(notiUrl, entity, String.class);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

}
