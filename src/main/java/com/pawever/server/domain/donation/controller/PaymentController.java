package com.pawever.server.domain.donation.controller;

import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.donation.dto.TossWebhookTO;
import com.pawever.server.domain.donation.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("api/payments")
    public ResponseEntity<ApiResponse> createPayment(@RequestParam String orderId,
                                                       @RequestParam long paymentAmount,
                                                       @RequestParam long donationId) {
        try {
            paymentService.createPayment(orderId, paymentAmount, donationId);
            return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ResponseCodeEnum.INVALID_REQUEST_ARGUMENT));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.fail(ResponseCodeEnum.UNKNOWN_SERVER_ERROR));
        }
    }

    @PostMapping("api/payments/verify")
    public ResponseEntity<ApiResponse> verifyPayment(@RequestParam String paymentKey,
                                                     @RequestParam String orderId,
                                                     @RequestParam long paymentAmount) {
        try {
            paymentService.verifyPayment(paymentKey, orderId, paymentAmount);
            return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ResponseCodeEnum.INVALID_REQUEST_ARGUMENT));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.fail(ResponseCodeEnum.UNKNOWN_SERVER_ERROR));
        }
    }
/*
    @PostMapping("api/payments/confirm")
    public ResponseEntity<ApiResponse> confirmPayment(@RequestParam String paymentKey,
                                                      @RequestParam String orderId,
                                                      @RequestParam long paymentAmount) {
        try {
            paymentService.confirmPayment(paymentKey, orderId, paymentAmount);
            return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ResponseCodeEnum.INVALID_REQUEST_ARGUMENT));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ResponseCodeEnum.PAYMENT_BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.fail(ResponseCodeEnum.UNKNOWN_SERVER_ERROR));
        }
    }

    @PostMapping("api/payments/webhook")
    public ResponseEntity<ApiResponse> handleTossWebhook(@RequestBody TossWebhookTO webhookRequest) {
        try {
            paymentService.processTossWebhook(webhookRequest);
            return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.fail(ResponseCodeEnum.UNKNOWN_SERVER_ERROR));
        }
    }
 */
}
