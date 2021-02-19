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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.util.UriComponentsBuilder;

import com.nx.lib.exception.BaseException;

@SuppressWarnings({ "rawtypes", "unchecked" })
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

        return exchange(builder.build().toUriString(), method, new HttpEntity("parameters", headers), urlParam, clz);
    }

    private <T> ResponseEntity<T> bodyRequest(HttpMethod method, String serviceId, String urlParam,
            Map<String, Object> bodyMap, Class<T> clz) {
        String token = this.checkToken();

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                                                           .scheme("http")
                                                           .host(this.getHost(serviceId, urlParam));

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

        return exchange(builder.build().toUriString(), method, new HttpEntity(bodyStr, headers), urlParam, clz);
    }

    // Return ResponseEntity : 'result', 'HttpStatusCode'
    private <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity requestEntity, String urlParam,
            Class<T> clz) {
        ResponseEntity<T> res;
        try {
            ResponseEntity<Map> crossApiResponse = rt.exchange(url, method, requestEntity, Map.class);

            logger.info("[{}] CrossAPIv2 Response Url [{}] Response [{}]", getProfile(), urlParam,
                    crossApiResponse.getStatusCode());

            Map<String, Object> crossApiResponseMap = crossApiResponse.getBody();

            Object result = crossApiResponseMap.get("result");

            if (result == null)
                return new ResponseEntity<>(crossApiResponse.getStatusCode());

            T typeResult = null;
            if (clz == Map.class) {
                typeResult = (T) result;
            } else if (clz == List.class) {
                typeResult = (T) result;
            }

            res = new ResponseEntity<>(typeResult, crossApiResponse.getStatusCode());
        } catch (Exception e) {

            T emptyObj = null;

            if (clz == Map.class) {
                emptyObj = (T) Collections.emptyMap();
            } else if (clz == List.class) {
                emptyObj = (T) Collections.emptyList();
            }

            if (e instanceof HttpStatusCodeException) {
                HttpStatusCodeException err = (HttpStatusCodeException) e;

                logger.info("[{}] CrossAPIv2 Response Url [{}] Response [{}]", getProfile(), urlParam,
                        err.getStatusCode());

                res = new ResponseEntity<>(emptyObj, err.getStatusCode());
            } else if (e instanceof ClassCastException) {
                logger.info(
                        "[{}] CrossAPIv2 Response Url [{}] Response [500] CrossApi Result Type Casting Exception [{}]",
                        getProfile(), urlParam, e.getMessage());
                res = new ResponseEntity<>(emptyObj, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                logger.info("[{}] CrossAPIv2 Response Url [{}] Response [500] Undefined Exception [{}]", getProfile(),
                        urlParam, e.getMessage());

                res = new ResponseEntity<>(emptyObj, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }

        return res;
    }

    private String checkToken() {
        if (this.authorizationToken == null || "".equals(this.authorizationToken)) {
            throw new BaseException("1000", "NOTFOUND EXCEPTION : Authorization Token이 필요합니다.");
        }

        return bearer(this.authorizationToken);
    }

    private List<ServiceInstance> getServiceInstance(String serviceId) {
        List<ServiceInstance> instances = super.discoveryClient.getInstances(serviceId);
        if (instances.isEmpty())
            throw new BaseException("1002", "EUREKA EXCEPTION : Eureka 인스턴스를 찾을 수 없습니다.");

        return instances;
    }

    private String getHost(String serviceId, String urlParam) {
        ServiceInstance serviceInstance = getServiceInstance(serviceId).get(0);
        return new StringBuilder(serviceInstance.getHost()).append(":")
                                                           .append(serviceInstance.getPort())
                                                           .append(urlParam)
                                                           .toString();
    }
}
