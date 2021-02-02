package com.nx.lib;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nx.lib.exception.BaseException;

public class NopsUtil {

    public static final String SERVER_FREEZING = "1"; // 프리징 서버
    public static final String SERVER_DEV = "2"; // 개발서버
    public static final String SERVER_DESIGN_TEST01 = "4"; // 기획테스트서버01
    public static final String SERVER_DESIGN_TEST02 = "5"; // 기획테스트서버02
    public static final String SERVER_DESIGN_TEST03 = "6"; // 기획테스트서버03
    public static final String SERVER_DESIGN_TEST04 = "7"; // 기획테스트서버04
    public static final String SERVER_DESIGN_TEST05 = "8"; // 기획테스트서버05
    public static final String SERVER_DESIGN_TEST06 = "3"; // 기획테스트서버06
    public static final String SERVER_DESIGN_TEST07 = "9"; // 기획테스트서버07
    public static final String SERVER_DESIGN_TEST08 = "10"; // 기획테스트서버08
    public static final String SERVER_QA = "18"; // QA 서버 (개발DB씀)
    public static final String SERVER_DAILYQA = "22"; // DailyQA 서버
    public static final String SERVER_BALANCE = "23"; // Balance 서버
    public static final String SERVER_POLISHING = "28"; // 폴리싱서버
    public static final String SERVER_FREEZINGFIX = "35"; // 프리징픽스
    public static final String SERVER_COMPANY = "47"; // 전사테스트 서버
    public static final String SERVER_FGT = "50"; // FGT서버
    
    public static final String SERVER_DEPLOY_QA = "60";
    public static final String SERVER_DEPLOY_QAFIX = "59"; 
    public static final String SERVER_DEPLOY_QA_OLD = "5000"; 
    public static final String SERVER_DEPLOY_QAFIX_OLD = "5000"; 

    public static final RestTemplate restTemplate = new RestTemplate();

    private static String PROFILES = null;

    public static String profiles() {
        if (PROFILES == null) {
            String springProfilesActive = System.getProperty("spring.profiles.active");
            PROFILES = (springProfilesActive == null) ? "default" : springProfilesActive;
        }
        return PROFILES;
    }

    public static boolean isLive() {
        return "production".equals(profiles());
    }

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
     * 
     * @return
     */
    public static String getUser() {
        if (RequestContextHolder.getRequestAttributes() != null)
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
                                                                                           .getRemoteUser();

        return "USER-Thread";
    }

    /**
     *
     * @param keyList
     * @param column
     * @return
     */
    public static int columnCheck(List<String> keyList, String column) {

        int chk = 0;
        for (String columnKey : keyList) {
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
                        if (isNumber(value)) {
                            double doubleVal = Double.parseDouble(value);
                            double result = doubleVal - (int) doubleVal;
                            if (result == 0) {
                                value = (int) doubleVal + "";
                            }
                        }

                        if (value.contains(",")) {
                            // 콤마가 내용안에 포함되어있고 쌍따옴표가 가 내용안에 포함되어 있으면 쌍따옴표를 2개로 바꿔줌.
                            value.replaceAll("\"", "\"\"");
                            // 콤마가 중간에 있으면 " 으로 감싸줌
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

    /**
     * 입력으로 받은 테이블을 읽어서 CSV 형식으로 변환한다.
     *
     * @param list
     * @return
     */
    public static String makeCSV(List<Map<String, Object>> list, boolean containsHeader) {
        try {

            if (list.size() > 0) {
                StringBuilder str = new StringBuilder();

                int i = 0;

                for (Map<String, Object> map : list) {

                    if (i == 0 && containsHeader) {
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
                        if (isNumber(value)) {
                            double doubleVal = Double.parseDouble(value);
                            double result = doubleVal - (int) doubleVal;
                            if (result == 0) {
                                value = (int) doubleVal + "";
                            }
                        }

                        if (value.contains(",")) {
                            // 콤마가 내용안에 포함되어있고 쌍따옴표가 가 내용안에 포함되어 있으면 쌍따옴표를 2개로 바꿔줌.
                            value.replaceAll("\"", "\"\"");
                            // 콤마가 중간에 있으면 " 으로 감싸줌
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

    public static boolean sendNotificationSlackTarget(String content, String target) {
        String recipients = "\"" + target + "\"";
        return sendNotifications(null, content, "", "black", recipients);
    }

    public static boolean sendNotificationChannel(String content, String channel) {
        String recipients = "\"#" + channel + "\"";
        return sendNotifications(null, content, "", "black", recipients);
    }

    public static boolean sendNotificationUser(String content, String user) {
        String recipients = "\"@" + user + "\"";
        return sendNotifications(null, content, "", "black", recipients);
    }

    public static boolean sendNotificationUsers(String content, List<String> userList) {
        String recipients = String.join("\",\"@", userList);
        recipients = "\"@" + recipients + "\"";
        return sendNotifications(null, content, "", "black", recipients);
    }

    public static boolean sendNotifications(String host, String content, String attachments, String color,
            String recipients) {

        try {
            String _host = (host == null) ? "http://noti.npixel.co.kr" : host;
            final String notiUrl = _host + "/api/notification/v1.0/send";

            HttpHeaders headers = new HttpHeaders();
            Charset utf8 = Charset.forName("UTF-8");
            MediaType mediaType = new MediaType("application", "json", utf8);
            headers.setContentType(mediaType);

            String requestJson = "{" + "\"data\": [{" + "\"send_type\": \"5\"," + "\"recipients\": [" + recipients
                    + "]," + "\"bot_name\": \"NOPS Noti Service\"," + "\"content\": \"" + content + "\","
                    + "\"attachments\":[{" + "\"text\":\"" + attachments + "\"," + "\"color\": \"" + color + "\"" + "}]"
                    + "}]" + "}";

            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

            restTemplate.postForObject(notiUrl, entity, String.class);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    /**
     * 현재 시간을 입력한 포맷에 맞게 출력
     * 
     * @param format ex) yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String now(String format) {

        try {

            if (format == null || "".equals(format) || "now".equals(format)) {
                format = "yyyy-MM-dd HH:mm:ss";
            }

            DateFormat dateFormat = new SimpleDateFormat(format);
            Date date = new Date();

            return dateFormat.format(date);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException();
        }
    }

    /**
     * Page 객체에 담긴 내용을 Map으로 변환하고 Map의 Key를 필터 할 수 있는 메서드
     * 
     * @param objectList
     * @param filter
     * @return
     */
    public static Map convertPageObjectToMapList(Page objectList, String... filter) {
        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(objectList);
        } catch (JsonProcessingException e) {
            json = "";
            e.printStackTrace();
        }

        Map<String, Object> map = convertJsonToMap(json, filter);

        map.remove("total");
        map.remove("pageable");

        Map<String, Object> pageableMap = new LinkedHashMap<>();
        Map<String, Object> pageableMapAttr = new LinkedHashMap<>();
        Map<String, Object> sortMapAttr = new LinkedHashMap<>();

        sortMapAttr.put("unsorted", objectList.getPageable().getSort().isUnsorted());
        sortMapAttr.put("sorted", objectList.getPageable().getSort().isSorted());

        pageableMapAttr.put("sort", sortMapAttr);
        pageableMapAttr.put("offset", objectList.getPageable().getOffset());
        pageableMapAttr.put("pageNumber", objectList.getPageable().getPageNumber());
        pageableMapAttr.put("pageSize", objectList.getPageable().getPageSize());
        pageableMapAttr.put("paged", objectList.getPageable().isPaged());
        pageableMapAttr.put("unpaged", objectList.getPageable().isUnpaged());

        pageableMap.put("pageable", pageableMapAttr);
        pageableMap.put("totalElements", objectList.getTotalElements());
        pageableMap.put("totalPages", objectList.getTotalPages());
        pageableMap.put("last", objectList.isLast());
        pageableMap.put("size", objectList.getSize());
        pageableMap.put("number", objectList.getNumber());
        pageableMap.put("sort", sortMapAttr);
        pageableMap.put("numberOfElements", objectList.getNumberOfElements());
        pageableMap.put("first", objectList.isFirst());

        map.putAll(pageableMap);

        // orderNum 세팅
        setOrderNum(map);

        return map;
    }

    /**
     * Page 객체에 담긴 내용을 Map으로 변환하고 Map의 Key를 필터 할 수 있는 메서드 ( 가공 안하고 싶을때 사용 )
     * 
     * @param json
     * @param filter
     * @return
     */
    public static Map<String, Object> convertJsonToMap(String json, String... filter) {

        Map<String, Object> map = new HashMap<>();
        // convert JSON string to Map
        ObjectMapper mapper = new ObjectMapper();
        try {

            map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });

            if (filter != null && filter.length > 0) {
                List<Map<String, Object>> mapList = (List) map.get("content");
                for (Map<String, Object> data : mapList) {
                    for (String str : filter) {
                        if (data.containsKey(str)) {
                            data.remove(str);
                        }

                    }
                }
            }

        } catch (JsonGenerationException e) {
            e.printStackTrace();
            throw new BaseException();
        } catch (JsonMappingException e) {
            e.printStackTrace();
            throw new BaseException();
        } catch (IOException e) {
            e.printStackTrace();
            throw new BaseException();
        }

        return map;
    }

    /**
     * orderNum 컬럼의 순번을 매길때 사용
     * 
     * @param dataMap
     * @return
     */
    public static Map<String, Object> setOrderNum(Map<String, Object> dataMap) {

        try {

            int totalElements = Integer.parseInt(dataMap.get("totalElements").toString()); // totalElements : 73
            int numberOfElements = Integer.parseInt(dataMap.get("numberOfElements").toString()); // numberOfElements :
                                                                                                 // 13
            int size = Integer.parseInt(dataMap.get("size").toString()); // size : 15
            int number = Integer.parseInt(dataMap.get("number").toString()); // number : 4

            int startOrder = (totalElements - (size * number)); // 13
            int endOrder = (startOrder - numberOfElements); // 0

            int cnt = 0;
            for (int i = startOrder; i > endOrder; i--) {
                (((List<Map<String, Object>>) dataMap.get("content")).get(cnt)).put("orderNum", i);
                cnt++;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException();
        }

        return dataMap;
    }
}
