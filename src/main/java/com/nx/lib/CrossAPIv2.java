package com.nx.lib;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.util.UriComponentsBuilder;

import com.nx.lib.exception.BaseException;

public class CrossAPIv2 extends CrossAPI {
    private String authorizationToken;

    public CrossAPIv2(DiscoveryClient discoveryClient) {
        super(discoveryClient);
    }

    public CrossAPIv2(DiscoveryClient discoveryClient, String authorizationToken) {
        super(discoveryClient);
        this.authorizationToken = authorizationToken;
    }

    public CrossAPIv2 authorizationToken(String authorizationToken) {
        if (authorizationToken == null || "".equals(authorizationToken)) {
            throw new UnsupportedOperationException();
        }
        this.authorizationToken = authorizationToken;
        return this;
    }

    public <T> ResponseEntity<T> GET(String serviceId, String urlParam, Class<T> clz) {
        return GET(serviceId, urlParam, Collections.emptyMap(), clz);
    }

    public <T> ResponseEntity<T> GET(String serviceId, String urlParam, Map<String, Object> queryParams, Class<T> clz) {
        return queryRequest(HttpMethod.GET, serviceId, urlParam, queryParams, clz);
    }

    public <T> ResponseEntity<T> POST(String serviceId, String urlParam, Class<T> clz) {
        return POST(serviceId, urlParam, Collections.emptyMap(), clz);
    }

    public <T> ResponseEntity<T> POST(String serviceId, String urlParam, Map<String, Object> bodyMap, Class<T> clz) {
        return bodyRequest(HttpMethod.POST, serviceId, urlParam, bodyMap, clz);
    }

    public <T> ResponseEntity<T> PUT(String serviceId, String urlParam, Class<T> clz) {
        return PUT(serviceId, urlParam, Collections.emptyMap(), clz);
    }

    public <T> ResponseEntity<T> PUT(String serviceId, String urlParam, Map<String, Object> bodyMap, Class<T> clz) {
        return bodyRequest(HttpMethod.PUT, serviceId, urlParam, bodyMap, clz);
    }

    public <T> ResponseEntity<T> DELETE(String serviceId, String urlParam, Class<T> clz) {
        return DELETE(serviceId, urlParam, Collections.emptyMap(), clz);
    }

    public <T> ResponseEntity<T> DELETE(String serviceId, String urlParam, Map<String, Object> queryParams,
            Class<T> clz) {
        return queryRequest(HttpMethod.DELETE, serviceId, urlParam, queryParams, clz);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <T> ResponseEntity<T> queryRequest(HttpMethod method, String serviceId, String urlParam,
            Map<String, Object> queryParams, Class<T> clz) {
        String token = this.checkToken();

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                                                           .scheme("http")
                                                           .host(this.getHost(serviceId, urlParam));

        if (!queryParams.isEmpty()) {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
                params.add(entry.getKey(), String.valueOf(entry.getValue()));
            }

            builder.queryParams(params);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        HttpEntity requestEntity = new HttpEntity("parameters", headers);
        ResponseEntity<T> res;

        try {
            res = rt.exchange(builder.build().toUriString(), method, requestEntity, clz);
            logger.info("[{}] CrossAPIv2 Response Url [{}] Response [{}]", getProfile(), urlParam, res.getStatusCode());
        } catch (HttpStatusCodeException e) {
            T emptyObj = null;
            if (clz == Map.class) {
                emptyObj = (T) Collections.emptyMap();
            } else if (clz == List.class) {
                emptyObj = (T) Collections.emptyList();
            }
            res = new ResponseEntity<>(emptyObj, e.getStatusCode());
            logger.info("[{}] CrossAPIv2 Response Url [{}] Response [{}]", getProfile(), urlParam, e.getStatusCode());
        } catch (Exception e) {
            throw new BaseException("1001", e.getMessage());
        }

        return res;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <T> ResponseEntity<T> bodyRequest(HttpMethod method, String serviceId, String urlParam,
            Map<String, Object> bodyMap, Class<T> clz) {
        String token = this.checkToken();

        String url = "http://" + getHost(serviceId, urlParam);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        String bodyStr = "";
        if (!bodyMap.isEmpty()) {
            try {
                bodyStr = objectMapper.writeValueAsString(bodyMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        HttpEntity requestEntity = new HttpEntity(bodyStr, headers);
        ResponseEntity<T> res = null;

        try {
            res = rt.exchange(url, method, requestEntity, clz);
            logger.info("[{}] CrossAPIv2 Response Url [{}] Response [{}]", getProfile(), urlParam, res.getStatusCode());
        } catch (HttpStatusCodeException e) {
            T emptyObj = null;
            if (clz == Map.class) {
                emptyObj = (T) Collections.emptyMap();
            } else if (clz == List.class) {
                emptyObj = (T) Collections.emptyList();
            }
            res = new ResponseEntity<>(emptyObj, e.getStatusCode());
            logger.info("[{}] CrossAPIv2 Response Url [{}] Response [{}]", getProfile(), urlParam, e.getStatusCode());
        } catch (Exception e) {
            throw new BaseException("1001", "UNKNOWN EXCEPTION : " + e.getMessage());
        }

        return res;
    }

    private String checkToken() {
        String token = (this.authorizationToken != null) ? this.authorizationToken : authorizationToken;
        if (token == null || "".equals(token)) {
            throw new BaseException("1000", "NOTFOUND EXCEPTION : Authorization Token");
        }

        return bearer(token);
    }

    private List<ServiceInstance> getServiceInstance(String serviceId) {
        return super.discoveryClient.getInstances(serviceId);
    }

    private String getHost(String serviceId, String urlParam) {
        ServiceInstance serviceInstance = getServiceInstance(serviceId).get(0);
        return new StringBuilder(serviceInstance.getHost()).append(":")
                                                           .append(serviceInstance.getPort())
                                                           .append(urlParam)
                                                           .toString();
    }
}
