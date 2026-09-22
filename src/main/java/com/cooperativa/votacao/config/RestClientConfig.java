package com.cooperativa.votacao.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    public RestTemplate restTemplate(IntegracaoProperties properties) {
        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(properties.getTimeoutMs()))
                .setReadTimeout(Duration.ofMillis(properties.getTimeoutMs()))
                .build();
    }

    @Bean
    public RestClient restClient(IntegracaoProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getUrl())
                .build();
    }
}
