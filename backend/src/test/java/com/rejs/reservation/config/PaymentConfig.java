package com.rejs.reservation.config;

import io.portone.sdk.server.payment.PaymentClient;
import io.portone.sdk.server.webhook.WebhookVerifier;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;


@TestConfiguration
public class PaymentConfig {

    @Bean
    public PaymentClient paymentClient(){
        return Mockito.mock(PaymentClient.class);
    }

    @Bean
    public WebhookVerifier webhookVerifier(){
        return Mockito.mock(WebhookVerifier.class);
    }
}
