package com.rejs.reservation.domain.payments.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("portone.secret")
public class PortOneProperties {
    private String apiSecret;
    private String storeId;
    private String webhookSecret;
}
