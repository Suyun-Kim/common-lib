package com.nx.lib.api.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class NopsHeader {

    /**
     * Audit-Header Json Format
     * "Audit-header" : {
     *  "gameId" : "",
     *  "worldNo" : 0,
     *  "notifyMessage" : "",
     *  "correlationId" : "",
     *  "afterDetail" : "",
     *  "beforeDetail" : "",
     *  "reason" : ""
     * }
     *
     */

    /**
     * 게이트웨이로 별도의 정보를 전달하기 위한 헤더를 만든다
     * @param worldId
     * @param notifyMessage
     * @param request
     * @param afterDetail
     * @param beforeDetail
     * @param reason
     * @return
     * @throws UnsupportedEncodingException
     */
    public static HttpHeaders gatewayAuditHeader (
            //String gameId,
            String worldId,
            String notifyMessage,
            HttpServletRequest request,
            String afterDetail,
            String beforeDetail,
            String reason
    ) throws UnsupportedEncodingException {
        HttpHeaders headers = new HttpHeaders();
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"gameId\" : \""+ "pr" + "\",");
        sb.append("\"worldNo\" : "+ worldId  + ",");
        sb.append("\"notifyMessage\" : \""+notifyMessage+"\",");
        sb.append("\"correlationId\" : \""+request.getHeader("correlationId")+"\",");
        sb.append("\"afterDetail\" : \""+afterDetail+"\",");
        sb.append("\"beforeDetail\" : \""+beforeDetail+"\",");
        sb.append("\"reason\" : \""+reason+"\"");
        sb.append("}");
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        // https://stackoverflow.com/questions/4737841/urlencoder-not-able-to-translate-space-character
        // URLEncoder.encode => UnsupportedEncodingException
        headers.add("Audit-header", URLEncoder.encode(sb.toString(), "UTF-8").replace("+", "%20"));

        return headers;
    }


}
