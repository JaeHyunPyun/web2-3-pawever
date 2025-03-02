package com.pawever.server.domain.donation.service;

import com.pawever.server.domain.donation.dto.PaymentTO;
import com.pawever.server.domain.donation.entity.Donation;
import com.pawever.server.domain.donation.entity.Payment;
import com.pawever.server.domain.donation.repository.DonationRepository;
import com.pawever.server.domain.donation.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {
    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Value("${tosspayments.secret-key}")
    private String tossSecreteKey;

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
        PaymentTO response = sendConfirmPaymentRequest(paymentKey, orderId, paymentAmount);
        updatePaymentStatus(payment, response);
    }

    private PaymentTO sendConfirmPaymentRequest(String paymentKey, String orderId, long paymentAmount) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(tossSecreteKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("paymentKey", paymentKey);
        body.put("orderId", orderId);
        body.put("amount", paymentAmount);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<PaymentTO> response = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/confirm",
                request,
                PaymentTO.class
        );

        return response.getBody();
    }

    private void updatePaymentStatus(Payment payment, PaymentTO response) {
        if ("DONE".equals(response.getPaymentStatus())) {
            payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
        } else {
            payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
        }
        payment.setApprovedAt(LocalDateTime.now());
        paymentRepository.save(payment);
    }


}
