package com.pawever.server.domain.donation.repository;

import com.pawever.server.domain.donation.entity.Payment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByPaymentId(String paymentId);

    @Query("SELECT p FROM Payment p WHERE p.donation.donationId = :donationId")
    Optional<Payment> findByDonationId(@Param("donationId") Long donationId);
}
