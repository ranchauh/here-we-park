package com.here.hackathon.car.parking.config;

import com.here.account.auth.OAuth1ClientCredentialsProvider;
import com.here.account.oauth2.HereAccessTokenProvider;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CPConfiguration {

    @Value("${app.id}")
    private String appId;

    @Bean
    public SparkSession sparkSession(){
       return SparkSession.builder()
                .appName(appId)
                .master("local[*]")
                .getOrCreate();
    }

    /**
     * Creates Spring RestTemplate bean
     * @return RestTemplate
     */
    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }

    /**
     * Creates a bean of type {@link HereAccessTokenProvider} provided condition in
     * @return {@link HereAccessTokenProvider} object
     */
    @Bean
    public HereAccessTokenProvider hereAccessTokenProvider(){
        return HereAccessTokenProvider.builder()
                .build();
    }

}
