/*
 * Copyright (C) 2019 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

package com.here.hackathon.car.parking.util;

import com.here.account.oauth2.HereAccessTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * A utility class for RestTemplate to invoke REST API calls required for the service broker
 */
@Component
@Slf4j
public class CPRestTemplate {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Autowired
    @Qualifier("restTemplate")
    private RestTemplate restTemplate;

    @Autowired(required = false)
    private HereAccessTokenProvider hereAccessTokenProvider;

    @PostConstruct
    public void init(){
        log.info("{}",hereAccessTokenProvider);
    }

    public <T> T getForEntity(String url, Class<T> responseType,Map<String, ?> queryParam,Object... uriVariables) {
        ResponseEntity<T> response = restTemplate.exchange(this.buildRequestUrl(url,queryParam), HttpMethod.GET,
                this.createHttpEntity(null),responseType,uriVariables);
        if(this.isResponseNotOk(response)){
            throw new RuntimeException(String.format("GET response status: %s, body: %s for url: %s",
                    response.getStatusCodeValue(),response.getBody(), url));
        }
        return response.getBody();
    }

    public <T,R> T putForEntity(String url, R requestBody, Class<T> responseType, Object... uriVariables){
        ResponseEntity<T> response = restTemplate.exchange(this.buildRequestUrl(url,null), HttpMethod.PUT,this.createHttpEntity(requestBody),responseType,uriVariables);
        if(this.isResponseNotOk(response)){
            throw new RuntimeException(String.format("PUT response status: %s, body: %s for url: %s",
                    response.getStatusCodeValue(),response.getBody(), url));
        }
        return response.getBody();
    }


    public <T,R> T patchForEntity(String url, R requestBody, Class<T> responseType, Object... uriVariables){
        ResponseEntity<T> response = restTemplate.exchange(this.buildRequestUrl(url,null),
                HttpMethod.PATCH,this.createHttpEntity(requestBody),responseType,uriVariables);
        if(this.isResponseNotOk(response)){
            throw new RuntimeException(String.format("response status: %s, body: %s for url: %s",
                    response.getStatusCodeValue(),response.getBody(), url));
        }
        return response.getBody();
    }


    public <T,R> T deleteForEntity(String url, R requestBody, Class<T> responseType, Map<String, ?> queryParam,Object... uriVariables){
        ResponseEntity<T> response = restTemplate.exchange(this.buildRequestUrl(url,queryParam),
                HttpMethod.DELETE,this.createHttpEntity(requestBody),responseType,uriVariables);
        if(this.isResponseNotOk(response)){
            throw new RuntimeException(String.format("DELETE response status: %s, body: %s for url: %s",
                    response.getStatusCodeValue(),response.getBody(), url));
        }
        return response.getBody();
    }

    private <T> boolean isResponseNotOk(ResponseEntity<T> response){
        return HttpStatus.SC_OK != response.getStatusCodeValue();
    }


    private String buildRequestUrl(String url, Map<String,?> queryParam) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if(Objects.nonNull(queryParam)){
            queryParam.forEach(builder::queryParam);
        }
        return builder.encode().toUriString();
    }

    private <R> HttpEntity<R> createHttpEntity(R requestBody) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if(Objects.nonNull(hereAccessTokenProvider)) {
            httpHeaders.add(AUTHORIZATION_HEADER, "Bearer " + hereAccessTokenProvider.getAccessToken());
        }
        return new HttpEntity<>(requestBody, httpHeaders);
    }
}
