package com.pawever.server.domain.donation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TossWebhookTO {
    private String paymentKey;
    private String orderId;
    private String status;
    private long amount;
    private String method;
    private String requestedAt;
    private String approvedAt;
}
