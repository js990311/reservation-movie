package com.rejs.reservation.domain.payments.config;

import io.portone.sdk.server.webhook.WebhookVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.portone.sdk.server.payment.PaymentClient;
import org.springframework.context.annotation.Profile;

@RequiredArgsConstructor
@Configuration
public class PaymentConfig {
    private final PortOneProperties portOneProperties;

    @Bean
    public PaymentClient paymentClient(){
        return new PaymentClient(portOneProperties.getApiSecret(), "https://api.portone.io", portOneProperties.getStoreId());
    }

    @Bean
    public WebhookVerifier webhookVerifier(){
        return new WebhookVerifier(portOneProperties.getWebhookSecret());
    }
}
