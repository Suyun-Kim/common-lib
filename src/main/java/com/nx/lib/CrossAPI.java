package com.nx.lib;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CrossAPI {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected DiscoveryClient discoveryClient;

    // TODO 전부 바꾼 후 제거
    private final String CROSS_API_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NTc4NzM1NjksInVzZ"
            + "XJfbmFtZSI6ImNyb3NzYXBpIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9DT01NT05fTk"
            + "8iXSwianRpIjoiMjk3ZTEyZmUtOTUzYi00M2RkLWE5MTctM2Y5YzFmOGY0MzMxIiw"
            + "iY2xpZW50X2lkIjoicGxhdGZvcm0iLCJzY29wZSI6WyJ0cnVzdCIsInJlYWQiLCJ3"
            + "cml0ZSJdfQ.hzR1BvtxMC63gGOOM8R1a2IjNbTzb-OwD_h2rDDcL-5tTLW-z_3rYz"
            + "0iVRrWpzz6C8JjXVB07m9JTE4W-PeOmIZj4nrgg92ExViOlfeB-1mI6TjWqU0ubQJ"
            + "C1Hx_wgSoZDACVz5PM3HE3RBoy2JwckbIKr0phvw7P98fvgrzH3Q";

    // TODO 전부 바꾼 후 제거
    private final String CROSS_API_TOKEN_LIVE_TMP = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2OTQ5NDIxNzksInVzZX"
            + "JfbmFtZSI6ImNyb3NzYXBpIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9DT01NT05fTk8i"
            + "XSwianRpIjoiODVlYTNiMjMtZjE1NC00YThkLThkOGUtNjkyODc3YzE5MDE1IiwiY2"
            + "xpZW50X2lkIjoiY2xpZW50SWQiLCJzY29wZSI6WyJ0cnVzdCIsInJlYWQiLCJ3cml0"
            + "ZSJdfQ.IBDQ4vYYnd92qmupnitDorqO-B3HJlY82aH92q6fyOw9MbzhkFWOIz3w1jP"
            + "P4pe1cPHGx5Nm_hfgmlyQTpl0IVsjX55yqn7jUMBMTb0W09c3goe9DNebIpQXPzXIh"
            + "-paSTCW40ZXyZGaG5W9bbEwtoek0kLaDEXmjYV1t9iK17ZKws5acEDxri_0YLWKCOZ"
            + "oDcg9UCUs2TSEwXbqVSfvEbTdvx6x_BlhG_zX4dRTjhgPQ-z9NyGRa0r8uM-5qPzxM"
            + "OXkzEdYu-BiSb-bgBmiOLT5MOLOWJNEKvXIQbNX35lz3iTEm1IF3BHwG3K8KfGkKUINMxIMcLjyFxXk0YDHsQ";

    protected final ObjectMapper objectMapper;
    protected final RestTemplate rt;
    protected final String systemProfiles;

    public CrossAPI(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
        this.systemProfiles = System.getProperty("spring.profiles.active");
        this.objectMapper = new ObjectMapper();
        this.rt = new RestTemplate();
    }

    public String getProfile() {
        return (this.systemProfiles == null) ? "default" : this.systemProfiles;
    }

    // TODO 기존 호환용 추후 제거 ( Authorization을 무조건 받도록 해야함 )

    @Deprecated
    public Object getCall(String serviceId, String urlParam) {
        return getCallWithAuthorization(serviceId, urlParam, Collections.emptyMap(), null);
    }

    public Object getCall(String serviceId, String urlParam, String authorizationToken) {
        return getCallWithAuthorization(serviceId, urlParam, Collections.emptyMap(), authorizationToken);
    }

    // TODO 기존 호환용 추후 제거 ( Authorization을 무조건 받도록 해야함 )

    @Deprecated
    public Object getCall(String serviceId, String urlParam, Map<String, Object> queryParams) {
        return getCallWithAuthorization(serviceId, urlParam, queryParams, null);
    }

    public Object getCall(String serviceId, String urlParam, Map<String, Object> queryParams,
            String authorizationToken) {
        return getCallWithAuthorization(serviceId, urlParam, queryParams, authorizationToken);
    }

    private Object getCallWithAuthorization(String serviceId, String urlParam, Map<String, Object> queryParams,
            String authorizationToken) {
        String token = (authorizationToken != null) ? authorizationToken : authorizationToken();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            params.add(entry.getKey(), String.valueOf(entry.getValue()));
        }

        List<ServiceInstance> list = discoveryClient.getInstances(serviceId);
        ServiceInstance serviceInstance = list.get(0); // 로드밸런스 관련 설정이 필요하다면 이곳을..

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                                                          .scheme("http")
                                                          .host(serviceInstance.getHost() + ":"
                                                                  + serviceInstance.getPort() + urlParam)
                                                          .queryParams(params)
                                                          .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", bearer(token));

        HttpEntity requestEntity = new HttpEntity(headers);

        ResponseEntity<Map> res = null;
        try {
            res = rt.exchange(uriComponents.toUriString(), HttpMethod.GET, requestEntity, Map.class);
            logger.info("[{}] CrossAPI Response Url [{}] Response [{}]", getProfile(), urlParam, res.getStatusCode());
        } catch (HttpStatusCodeException e) {
            logger.info("[{}] CrossAPI Response Url [{}] Response [{}]", getProfile(), urlParam, e.getStatusCode());
            return Collections.emptyMap();
        }

        return (res == null) ? Collections.emptyMap() : res.getBody().get("result");
    }

    // TODO 기존 호환용 추후 제거 ( Authorization을 무조건 받도록 해야함 )
    @Deprecated
    public HttpStatus postCall(String serviceId, String urlParam, Map<String, Object> bodyMap) {
        return postCallWithAuthorization(serviceId, urlParam, bodyMap, null);
    }

    public HttpStatus postCall(String serviceId, String urlParam, Map<String, Object> bodyMap,
            String authorizationToken) {
        return postCallWithAuthorization(serviceId, urlParam, bodyMap, authorizationToken);
    }

    private HttpStatus postCallWithAuthorization(String serviceId, String urlParam, Map<String, Object> bodyMap,
            String authorizationToken) {
        String token = (authorizationToken != null) ? authorizationToken : authorizationToken();

        List<ServiceInstance> list = discoveryClient.getInstances(serviceId);

        ServiceInstance serviceInstance = list.get(0); // 로드밸런스 관련 설정이 필요하다면 이곳을..
        String url = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + urlParam;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", bearer(token));

        String bodyStr = "";
        try {
            bodyStr = objectMapper.writeValueAsString(bodyMap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpEntity requestEntity = new HttpEntity(bodyStr, headers);
        try {
            ResponseEntity<Map> res = rt.exchange(url, HttpMethod.POST, requestEntity, Map.class);
            logger.info("[{}] CrossAPI Response Url [{}] Response [{}]", getProfile(), urlParam, res.getStatusCode());
            return res.getStatusCode();
        } catch (HttpStatusCodeException e) {
            logger.info("[{}] CrossAPI Response Url [{}] Response [{}]", getProfile(), urlParam, e.getStatusCode());
            return e.getStatusCode();
        }
    }

    public ResponseEntity<Map> postResponseCall(String serviceId, String urlParam, Map<String, Object> bodyMap,
                               String authorizationToken) {
        return postResponseCallWithAuthorization(serviceId, urlParam, bodyMap, authorizationToken);
    }

    private ResponseEntity<Map> postResponseCallWithAuthorization(String serviceId, String urlParam, Map<String, Object> bodyMap,
                                                 String authorizationToken) {
        String token = (authorizationToken != null) ? authorizationToken : authorizationToken();

        List<ServiceInstance> list = discoveryClient.getInstances(serviceId);

        ServiceInstance serviceInstance = list.get(0); // 로드밸런스 관련 설정이 필요하다면 이곳을..
        String url = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + urlParam;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", bearer(token));

        String bodyStr = "";
        try {
            bodyStr = objectMapper.writeValueAsString(bodyMap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpEntity requestEntity = new HttpEntity(bodyStr, headers);
        try {
            ResponseEntity<Map> res = rt.exchange(url, HttpMethod.POST, requestEntity, Map.class);
            logger.info("[{}] CrossAPI Response Url [{}] Response [{}]", getProfile(), urlParam, res.getStatusCode());
            return res;
        } catch (HttpStatusCodeException e) {
            logger.info("[{}] CrossAPI Response Url [{}] Response [{}]", getProfile(), urlParam, e.getStatusCode());
            return new ResponseEntity<>(e.getStatusCode());
        }
    }

    // TODO 기존 호환용 추후 제거 ( Authorization을 무조건 받도록 해야함 )
    @Deprecated
    public HttpStatus putCall(String serviceId, String urlParam, Map<String, Object> bodyMap) {
        return putCallWithAuthorization(serviceId, urlParam, bodyMap, null);
    }

    public HttpStatus putCall(String serviceId, String urlParam, Map<String, Object> bodyMap,
            String authorizationToken) {
        return putCallWithAuthorization(serviceId, urlParam, bodyMap, authorizationToken);
    }

    private HttpStatus putCallWithAuthorization(String serviceId, String urlParam, Map<String, Object> bodyMap,
            String authorizationToken) {
        String token = (authorizationToken != null) ? authorizationToken : authorizationToken();

        List<ServiceInstance> list = discoveryClient.getInstances(serviceId);

        ServiceInstance serviceInstance = list.get(0); // 로드밸런스 관련 설정이 필요하다면 이곳을..
        String url = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + urlParam;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", bearer(token));

        String bodyStr = "";
        try {
            bodyStr = objectMapper.writeValueAsString(bodyMap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpEntity requestEntity = new HttpEntity(bodyStr, headers);
        try {
            ResponseEntity<Map> res = rt.exchange(url, HttpMethod.PUT, requestEntity, Map.class);
            logger.info("[{}] CrossAPI Response Url [{}] Response [{}]", getProfile(), urlParam, res.getStatusCode());
            return res.getStatusCode();
        } catch (HttpStatusCodeException e) {
            logger.info("[{}] CrossAPI Response Url [{}] Response [{}]", getProfile(), urlParam, e.getStatusCode());
            return e.getStatusCode();
        }
    }

    public ResponseEntity<Map> putResponseCall(String serviceId, String urlParam, Map<String, Object> bodyMap,
                              String authorizationToken) {
        return putResponseCallWithAuthorization(serviceId, urlParam, bodyMap, authorizationToken);
    }

    private ResponseEntity<Map> putResponseCallWithAuthorization(String serviceId, String urlParam, Map<String, Object> bodyMap,
                                                String authorizationToken) {
        String token = (authorizationToken != null) ? authorizationToken : authorizationToken();

        List<ServiceInstance> list = discoveryClient.getInstances(serviceId);

        ServiceInstance serviceInstance = list.get(0); // 로드밸런스 관련 설정이 필요하다면 이곳을..
        String url = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + urlParam;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", bearer(token));

        String bodyStr = "";
        try {
            bodyStr = objectMapper.writeValueAsString(bodyMap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpEntity requestEntity = new HttpEntity(bodyStr, headers);
        try {
            ResponseEntity<Map> res = rt.exchange(url, HttpMethod.PUT, requestEntity, Map.class);
            logger.info("[{}] CrossAPI Response Url [{}] Response [{}]", getProfile(), urlParam, res.getStatusCode());
            return res;
        } catch (HttpStatusCodeException e) {
            logger.info("[{}] CrossAPI Response Url [{}] Response [{}]", getProfile(), urlParam, e.getStatusCode());
            return new ResponseEntity<>(e.getStatusCode());
        }
    }


    // TODO 기존 호환용 추후 제거 ( Authorization을 무조건 받도록 해야함 )
    @Deprecated
    public HttpStatus deleteCall(String serviceId, String urlParam, Map<String, Object> queryParams) {
        return deleteCallWithAuthorization(serviceId, urlParam, queryParams, null);
    }

    public HttpStatus deleteCall(String serviceId, String urlParam, Map<String, Object> queryParams,
            String authorizationToken) {
        return deleteCallWithAuthorization(serviceId, urlParam, queryParams, authorizationToken);
    }

    private HttpStatus deleteCallWithAuthorization(String serviceId, String urlParam, Map<String, Object> queryParams,
            String authorizationToken) {
        String token = (authorizationToken != null) ? authorizationToken : authorizationToken();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            params.add(entry.getKey(), String.valueOf(entry.getValue()));
        }

        List<ServiceInstance> list = discoveryClient.getInstances(serviceId);
        ServiceInstance serviceInstance = list.get(0); // 로드밸런스 관련 설정이 필요하다면 이곳을..

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                                                          .scheme("http")
                                                          .host(serviceInstance.getHost() + ":"
                                                                  + serviceInstance.getPort() + urlParam)
                                                          .queryParams(params)
                                                          .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", bearer(token));

        ResponseEntity<Map> res = null;
        HttpEntity requestEntity = new HttpEntity(headers);
        try {
            res = rt.exchange(uriComponents.toUriString(), HttpMethod.DELETE, requestEntity, Map.class);
            logger.info("[{}] CrossAPI Response Url [{}] Response [{}]", getProfile(), urlParam, res.getStatusCode());
            return res.getStatusCode();
        } catch (HttpStatusCodeException e) {
            logger.info("[{}] CrossAPI Response Url [{}] Response [{}]", getProfile(), urlParam, e.getStatusCode());
            return e.getStatusCode();
        }
    }

    // TODO 전부 바꾼 후 제거
    @Deprecated
    private String authorizationToken() {
        String env = getProfile();
        return (env != null && (NopsUtil.isLive() || env.equals("cbt"))) ? CROSS_API_TOKEN_LIVE_TMP
                : CROSS_API_TOKEN;
    }

    protected String bearer(String token) {
        return (token.startsWith("Bearer")) ? token : "Bearer " + token;
    }
}
