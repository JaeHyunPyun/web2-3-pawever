package com.pawever.server.domain.donation.service;

import com.pawever.server.domain.donation.dto.PaymentTO;
import com.pawever.server.domain.donation.dto.TossCancelTO;
import com.pawever.server.domain.donation.dto.TossWebhookTO;
import com.pawever.server.domain.donation.entity.Donation;
import com.pawever.server.domain.donation.entity.Payment;
import com.pawever.server.domain.donation.repository.DonationRepository;
import com.pawever.server.domain.donation.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        if (paymentAmount != payment.getPaymentAmount()) {
            throw new IllegalArgumentException("Invalid amount: " + paymentAmount);
        }
        PaymentTO response = sendConfirmPaymentRequest(paymentKey, orderId, paymentAmount);
        updatePaymentStatus(payment, response, paymentKey);
    }

    private PaymentTO sendConfirmPaymentRequest(String paymentKey, String orderId, long paymentAmount) {
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

        return response.getBody();
    }

    private void updatePaymentStatus(Payment payment, PaymentTO response, String paymentKey) {
        if ("DONE".equals(response.getPaymentStatus())) {
            payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
        } else {
            payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
            cancelPayment(paymentKey);
        }
        payment.setApprovedAt(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    @Transactional
    public void cancelPayment(String paymentKey) {
        String cancelReason = "서버 오류로 결제에 실패했습니다";
        Payment payment = paymentRepository.findByPaymentId(paymentKey)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다: " + paymentKey));

        if (payment.getPaymentStatus() == Payment.PaymentStatus.CANCELED) {
            throw new IllegalStateException("이미 취소된 결제입니다.");
        }

        TossCancelTO response = sendCancelPaymentRequest(paymentKey, cancelReason);

        payment.setPaymentStatus(Payment.PaymentStatus.CANCELED);
        payment.setCanceledAt(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    private TossCancelTO sendCancelPaymentRequest(String paymentKey, String cancelReason) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String basicAuthValue = "Basic " + Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes());
        headers.set("Authorization", basicAuthValue);

        Map<String, String> body = new HashMap<>();
        body.put("cancelReason", cancelReason);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<TossCancelTO> response = restTemplate.exchange(
                url, HttpMethod.POST, request, TossCancelTO.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("토스 결제 취소 요청 실패");
        }

        return response.getBody();
    }

    public void processTossWebhook(TossWebhookTO webhookRequest) {
        String paymentKey = webhookRequest.getPaymentKey();
        String orderId = webhookRequest.getOrderId();
        String status = webhookRequest.getStatus();

        switch (status) {
            case "DONE":
                handlePaymentSuccess(paymentKey, orderId, webhookRequest);
                break;
            case "FAILED":
                handlePaymentFailure(paymentKey, orderId);
                break;
            case "EXPIRED":
                handlePaymentExpired(paymentKey, orderId);
                break;
            default:
                throw new IllegalArgumentException("알 수 없는 결제 상태: " + status);
        }
    }

    private void handlePaymentSuccess(String paymentKey, String orderId, TossWebhookTO webhookRequest) {
        Payment payment = paymentRepository.findByPaymentId(paymentKey)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보가 존재하지 않습니다: " + paymentKey));

        payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
        payment.setApprovedAt(LocalDateTime.parse(webhookRequest.getApprovedAt()));
        paymentRepository.save(payment);
    }

    private void handlePaymentFailure(String paymentKey, String orderId) {
        Payment payment = paymentRepository.findByPaymentId(paymentKey)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보가 존재하지 않습니다: " + paymentKey));

        payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
        paymentRepository.save(payment);
    }

    private void handlePaymentExpired(String paymentKey, String orderId) {
        Payment payment = paymentRepository.findByPaymentId(paymentKey)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보가 존재하지 않습니다: " + paymentKey));

        payment.setPaymentStatus(Payment.PaymentStatus.EXPIRED);
        paymentRepository.save(payment);
    }


}
