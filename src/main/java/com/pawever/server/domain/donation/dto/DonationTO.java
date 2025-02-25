package com.pawever.server.domain.donation.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DonationTO {
    private String donorName;
    private String donorMessage;
    private Long donationAmount;
    private LocalDateTime createdAt;
}
