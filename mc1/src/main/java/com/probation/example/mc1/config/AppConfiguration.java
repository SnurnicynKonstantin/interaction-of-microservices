package com.probation.example.mc1.config;

import com.probation.example.mc1.helper.DataSenderComposite;
import com.probation.example.mc1.clients.JaegerClient;
import com.probation.example.mc1.helper.ServiceDataSender;
import com.probation.example.mc1.clients.WebsocketClient;
import com.probation.example.mc1.service.MessageService;
import io.opentracing.Tracer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class AppConfiguration {

    @Bean
    WebsocketClient websocketClient(@Value("${websocket.server.uri}") String serviceUri) throws URISyntaxException {
        return new WebsocketClient(new URI(serviceUri));
    }

    @Bean
    JaegerClient jaegerClient(Tracer tracer) {
        return new JaegerClient(tracer);
    }

    @Bean
    DataSenderComposite dataSenderComposite(WebsocketClient websocketClient, JaegerClient jaegerClient) {
        DataSenderComposite dataSenderComposite = new DataSenderComposite();
        dataSenderComposite.add(websocketClient);
        dataSenderComposite.add(jaegerClient);
        return dataSenderComposite;
    }

    @Bean
    ServiceDataSender serviceDataSender(@Value("${running.time}") Long runningTimeDuration, @Value("${running.delay.time}") Integer runningDelayTime,
                                        WebsocketClient websocketClient, MessageService messageService, DataSenderComposite dataSenderComposite) {
        return new ServiceDataSender(runningTimeDuration, runningDelayTime, websocketClient, messageService, dataSenderComposite);
    }

    @Bean
    public Tracer tracer(@Value("${spring.application.name}") String applicationName) {
        return io.jaegertracing.Configuration.fromEnv(applicationName).getTracer();
    }
}
