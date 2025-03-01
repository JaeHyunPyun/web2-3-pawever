package com.pawever.server.domain.donation.controller;

import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.donation.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("api/payments")
    public ResponseEntity<ApiResponse> getPaymentsInfo(@RequestParam String paymentKey,
                                                       @RequestParam String orderId,
                                                       @RequestParam long paymentAmount,
                                                       @RequestParam long donationId) {
        try {
            paymentService.getPaymentsInfo(paymentKey, orderId, paymentAmount, donationId);
            return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.fail(ResponseCodeEnum.UNKNOWN_SERVER_ERROR));
        }
    }
}
