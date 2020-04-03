package com.nx.lib;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

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

    /**
     * CrossAPI Get 요청
     * @param serviceId
     * @param urlParam
     * @param queryParams
     * @return
     */
    public Object getCall(String serviceId, String urlParam, Map<String, Object> queryParams) {

        RestTemplate rt = new RestTemplate();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            params.add(entry.getKey(), String.valueOf(entry.getValue()));
        }

        List<ServiceInstance> list = discoveryClient.getInstances(serviceId);
        ServiceInstance serviceInstance = list.get(0); //로드밸런스 관련 설정이 필요하다면 이곳을..

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http").host(serviceInstance.getHost() + ":" + serviceInstance.getPort() + urlParam)
                .queryParams(params).build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", CROSS_API_TOKEN);
        HttpEntity requestEntity =  new HttpEntity("parameters", headers);

        ResponseEntity<Map> res = rt.exchange(uriComponents.toUriString(), HttpMethod.GET, requestEntity, Map.class);

        int httpStatus = res.getStatusCodeValue();

        if (httpStatus != 200) {
            return new HashMap<>();
        }

        return res.getBody().get("result");
    }

    /**
     * CrossAPI GET 요청
     * @param serviceId
     * @param urlParam
     * @return
     */
    public Object getCall(String serviceId, String urlParam) {
        return this.getCall(serviceId, urlParam, new HashMap<>());
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
