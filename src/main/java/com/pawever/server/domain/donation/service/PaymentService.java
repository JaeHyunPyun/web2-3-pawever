package com.pawever.server.domain.donation.service;

import com.pawever.server.domain.donation.dto.PaymentTO;
import com.pawever.server.domain.donation.entity.Donation;
import com.pawever.server.domain.donation.entity.Payment;
import com.pawever.server.domain.donation.repository.DonationRepository;
import com.pawever.server.domain.donation.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {
    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Value("${tosspayments.secret-key}")
    private String tossSecretKey;

    @Transactional
    public void createPayment(String orderId, long paymentAmount, long donationId) {
        Payment payment = new Payment();
        payment.setPaymentId(orderId);

        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid donation ID: " + donationId));
        payment.setDonation(donation);
        payment.setPgProvider("TossPayments");
        payment.setPaymentAmount(paymentAmount);
        payment.setRequestedAt(LocalDateTime.now());
        payment.setPaymentStatus(Payment.PaymentStatus.PENDING);

        paymentRepository.save(payment);
    }

    @Transactional
    public void verifyPayment(String paymentKey, String orderId, long paymentAmount) {
        Payment payment = paymentRepository.findByPaymentId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID: " + orderId));
        if (paymentAmount != payment.getPaymentAmount()) {
            payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new IllegalArgumentException("Invalid amount: " + paymentAmount);
        }
        payment.setPgTid(paymentKey);
        paymentRepository.save(payment);
    }

    public void confirmPayment(String paymentKey, String orderId, long paymentAmount) {
        ResponseEntity<PaymentTO> response = requestConfirm(paymentKey, orderId, paymentAmount);
        System.out.println("response code: "+response.getStatusCode());
        System.out.println("결제 정보 조회 시작");

        Payment payment = paymentRepository.findByPaymentId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID: " + orderId));
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            try {
                payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
                payment.setApprovedAt(LocalDateTime.now());
                paymentRepository.save(payment);
            } catch (Exception e) {
                cancelPayment(paymentKey, orderId);
                throw new RuntimeException("결제 정보 저장 실패", e);
            }
        } else {
            payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
            throw new RuntimeException("결제 승인 요청 실패: " + response.getStatusCode());
        }
    }

    private ResponseEntity<PaymentTO> requestConfirm(String paymentKey, String orderId, long paymentAmount) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String basicAuthValue = "Basic " + Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes());
        headers.set("Authorization", basicAuthValue);
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
        return response;
    }

    public void cancelPayment(String paymentKey, String orderId) {
        System.out.println("결제 정보 저장 실패. 취소 요청 시작");
        String cancelReason = "서버 오류로 결제에 실패했습니다";
        Payment payment = paymentRepository.findByPaymentId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다: " + paymentKey));

        if (payment.getPaymentStatus() == Payment.PaymentStatus.CANCELED) {
            throw new IllegalStateException("이미 취소된 결제입니다.");
        }

        sendCancelPaymentRequest(paymentKey, cancelReason);

        payment.setPaymentStatus(Payment.PaymentStatus.CANCELED);
        payment.setCanceledAt(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    private void sendCancelPaymentRequest(String paymentKey, String cancelReason) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String basicAuthValue = "Basic " + Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes());
        headers.set("Authorization", basicAuthValue);

        Map<String, String> body = new HashMap<>();
        body.put("cancelReason", cancelReason);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, request, Void.class);
        } catch (RestClientException e) {
            throw new RuntimeException("결제 취소 요청 중 오류 발생: " + e.getMessage());
        }

    }


}
