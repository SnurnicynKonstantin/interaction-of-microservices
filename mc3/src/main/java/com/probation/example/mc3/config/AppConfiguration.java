package com.probation.example.mc3.config;

import io.opentracing.Tracer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfiguration {

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Tracer tracer(@Value("${spring.application.name}") String applicationName) {
        return io.jaegertracing.Configuration.fromEnv(applicationName).getTracer();
    }
}
