package com.onclass.ms_capability.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient technologyWebClient(
        WebClient.Builder builder,
        @Value("external.ms-technology.base-url") String baseUrl
    ) {
        return builder
            .baseUrl(baseUrl)
            .build();
    }
}
