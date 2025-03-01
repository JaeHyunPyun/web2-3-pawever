package com.pawever.server.domain.donation.dto;

import com.pawever.server.domain.user.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class DonationTO {
    private Long userId;
    private Long donationId;
    private String donorName;
    private String donorMessage;
    private Long donationAmount;
    private String createdAt;

    public void setCreatedAt(LocalDateTime createdAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        this.createdAt = createdAt.format(formatter);
    }
}
