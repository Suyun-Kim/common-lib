package com.nx.lib;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrossAPI {

    private DiscoveryClient discoveryClient;
    private static final String CROSS_API_TOKEN =
            "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NTc4NzM1NjksInVzZ" +
                    "XJfbmFtZSI6ImNyb3NzYXBpIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9DT01NT05fTk" +
                    "8iXSwianRpIjoiMjk3ZTEyZmUtOTUzYi00M2RkLWE5MTctM2Y5YzFmOGY0MzMxIiw" +
                    "iY2xpZW50X2lkIjoicGxhdGZvcm0iLCJzY29wZSI6WyJ0cnVzdCIsInJlYWQiLCJ3" +
                    "cml0ZSJdfQ.hzR1BvtxMC63gGOOM8R1a2IjNbTzb-OwD_h2rDDcL-5tTLW-z_3rYz" +
                    "0iVRrWpzz6C8JjXVB07m9JTE4W-PeOmIZj4nrgg92ExViOlfeB-1mI6TjWqU0ubQJ" +
                    "C1Hx_wgSoZDACVz5PM3HE3RBoy2JwckbIKr0phvw7P98fvgrzH3Q";
    private final ObjectMapper objectMapper;

    public CrossAPI(
            DiscoveryClient discoveryClient
    ) {
        this.discoveryClient = discoveryClient;
        objectMapper = new ObjectMapper();
    }

    public HashMap getCall(String serviceId, String urlParam) {
        RestTemplate rt = new RestTemplate();
        List<ServiceInstance> list = discoveryClient.getInstances(serviceId);
        ServiceInstance serviceInstance = list.get(0); //로드밸런스 관련 설정이 필요하다면 이곳을..
        String url = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort()
                + urlParam;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", CROSS_API_TOKEN);
        HttpEntity requestEntity =  new HttpEntity("parameters", headers);

        ResponseEntity<Map> res = rt.exchange(url, HttpMethod.GET, requestEntity, Map.class);

        return (HashMap)res.getBody().get("result");
    }

    public HttpStatus postCall(String serviceId, String urlParam, HashMap bodyMap) {
        RestTemplate rt = new RestTemplate();
        List<ServiceInstance> list = discoveryClient.getInstances(serviceId);
        ServiceInstance serviceInstance = list.get(0); //로드밸런스 관련 설정이 필요하다면 이곳을..
        String url = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort()
                + urlParam;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", CROSS_API_TOKEN);

        String bodyStr = "";
        try {
            bodyStr = objectMapper.writeValueAsString(bodyMap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpEntity requestEntity =  new HttpEntity(bodyStr, headers);
        ResponseEntity<Map> res = rt.exchange(url, HttpMethod.POST, requestEntity, Map.class);

        return res.getStatusCode();
    }
}
