package com.pawever.server.domain.donation.service;

import com.pawever.server.domain.donation.entity.Donation;
import com.pawever.server.domain.donation.entity.Payment;
import com.pawever.server.domain.donation.repository.DonationRepository;
import com.pawever.server.domain.donation.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentService {
    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Transactional
    public void getPaymentsInfo(String orderId, long paymentAmount, long donationId) {
        Payment payment = new Payment();
        payment.setPaymentId(orderId);

        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid donation ID: " + donationId));
        payment.setDonation((donation));
        payment.setPgProvider("TossPayments");
        payment.setPaymentAmount(paymentAmount);
        payment.setRequestedAt(LocalDateTime.now());
        payment.setPaymentStatus(Payment.PaymentStatus.PENDING);
    }

    @Transactional
    public void requestConfirmPayment(String paymentKey, String orderId, long paymentAmount) {
        Payment payment = paymentRepository.findByPaymentId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID: " + orderId));
        if (paymentAmount == payment.getPaymentAmount()) {
            throw new IllegalArgumentException("Invalid amount: " + paymentAmount);
        }
    }


}
