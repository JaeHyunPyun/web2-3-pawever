package com.pawever.server.domain.donation.entity;

import com.pawever.server.domain.user.entity.jpa.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "donation")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long donationId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

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
