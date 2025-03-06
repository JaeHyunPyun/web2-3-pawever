package com.pawever.server.domain.donation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentTO {
    private String paymentId;
    private long paymentAmount;
    private String pgTid;
    private String requestedAt;
    private String approvedAt;
    private String paymentStatus;
    private String paymentMethod;
}
