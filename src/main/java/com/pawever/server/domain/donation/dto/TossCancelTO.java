package com.pawever.server.domain.donation.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TossCancelTO {
    private String paymentKey;
    private String status;  // "CANCELED"
    private String cancelReason;
    private LocalDateTime canceledAt;
}
