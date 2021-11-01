package com.probation.example.mc2.config;

import com.probation.example.mc2.controller.MessageHandler;
import io.opentracing.Tracer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class AppConfiguration {

    @Bean
    Producer kafkaProducer(@Value("${kafka.host}") String kafkaHost) {
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaHost);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        return new KafkaProducer<>(props);
    }

    @Bean
    MessageHandler messageHandler(Producer kafkaProducer, @Value("${kafka.topic}") String kafkaTopic, Tracer tracer) {
        return new MessageHandler(kafkaProducer, kafkaTopic, tracer);
    }

    @Bean
    public Tracer initTracer(@Value("${spring.application.name}") String applicationName) {
        return io.jaegertracing.Configuration.fromEnv(applicationName).getTracer();
    }
}
