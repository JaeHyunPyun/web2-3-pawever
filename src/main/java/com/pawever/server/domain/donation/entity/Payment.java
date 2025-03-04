package com.pawever.server.domain.donation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Setter
@Getter
public class Payment {
    @Id
    @Column(name = "payment_id")
    private String paymentId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "donation_id", nullable = false)
    private Donation donation;

    @Column(name = "pg_provider", length = 50)
    private String pgProvider;

    @Column(name = "payment_amount", nullable = false)
    private Long paymentAmount;

    @Column(name = "pg_tid", length = 255)
    private String pgTid;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    public enum PaymentStatus {
        PENDING, SUCCESS, FAILED, EXPIRED, CANCELED
    }

    public enum PaymentMethod {
        CARD, EASYPAY
    }
}
