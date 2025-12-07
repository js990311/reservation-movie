package com.rejs.reservation.domain.payments.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@ConfigurationProperties("portone.secret")
public class PortOneProperties {
    private String apiSecret;
    private String storeId;
    private String webhookSecret;
}
