package com.pawever.server.domain.donation.controller;

import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.donation.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Tag(name = "결제 API", description = "결제 및 결제 승인 관련 API")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @Operation(summary = "결제 요청 API")
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

    @PostMapping("api/payments/confirm")
    @Operation(summary = "결제 승인 API")
    public ResponseEntity<ApiResponse> confirmPayment(@RequestParam String paymentKey,
                                                      @RequestParam String orderId,
                                                      @RequestParam long paymentAmount) {
        try {
            paymentService.verifyPayment(paymentKey, orderId, paymentAmount);
            paymentService.confirmPayment(paymentKey, orderId, paymentAmount);
            return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS));
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.fail(ResponseCodeEnum.INVALID_REQUEST_ARGUMENT));
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.fail(ResponseCodeEnum.PAYMENT_BAD_REQUEST));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponse.fail(ResponseCodeEnum.UNKNOWN_SERVER_ERROR));
        }
    }

}
