package com.pawever.server.domain.donation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "donation")
@Getter
@Setter
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long donationId;

    @Column(length = 50)
    private String donorName;

    @Column(length = 255)
    private String donorMessage;

    @Column(nullable = false)
    private Long donationAmount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
