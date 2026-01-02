package com.rejs.reservation.domain.payments.controller;

import com.rejs.reservation.domain.payments.facade.PaymentValidateFacade;
import io.portone.sdk.server.errors.WebhookVerificationException;
import io.portone.sdk.server.webhook.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payment/webhook")
public class WebhookController {
    private final WebhookVerifier webhookVerifier;
    private final PaymentValidateFacade paymentValidateFacade;

    @PostMapping
    public ResponseEntity<Void> handleWebhook(
        @RequestBody String body,
        @RequestHeader("webhook-id") String webHookId,
        @RequestHeader("webhook-timestamp") String webhookTimestamp,
        @RequestHeader("webhook-timestamp") String webhookSignature
    ){
        Webhook webhook;
        try {
            webhook = webhookVerifier.verify(body, webHookId, webhookTimestamp, webhookSignature);
        } catch (WebhookVerificationException e) {
            throw new RuntimeException(e);
        }

        if(webhook instanceof WebhookTransaction webhookTransaction){
            if(webhookTransaction instanceof WebhookTransactionPaid webhookTransactionPaid){
                // 결제 완료시
                String paymentId = webhookTransactionPaid.getData().getPaymentId();
                paymentValidateFacade.validate(paymentId);
            }
        }
        return ResponseEntity.ok().build();
    }
}
