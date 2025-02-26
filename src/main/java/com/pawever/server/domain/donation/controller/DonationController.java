package com.pawever.server.domain.donation.controller;

import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.donation.dto.DonationTO;
import com.pawever.server.domain.donation.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DonationController {
    @Autowired
    private DonationService donationService;

    @PostMapping("api/donations")
    public ResponseEntity<ApiResponse> createDonation(@RequestBody Map<String, Object> request) {
        try {
            donationService.createDonation(request);
            return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ResponseCodeEnum.INVALID_REQUEST_ARGUMENT));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ResponseCodeEnum.UNKNOWN_SERVER_ERROR));
        }
    }

    @GetMapping("api/users/staff/donations")
    public ResponseEntity<ApiResponse> getAllDonations() {
        try {
            List<DonationTO> donations = donationService.getAllDonations();
            return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, donations));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ResponseCodeEnum.UNKNOWN_SERVER_ERROR));
        }
    }

    @GetMapping("api/users/donations/{user_id}")
    public ResponseEntity<ApiResponse> getDonationByUser(@PathVariable Long user_id) {
        try {
            List<DonationTO> donationsByUser = donationService.getDonationByUser(user_id);
            return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, donationsByUser));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(ApiResponse.fail(ResponseCodeEnum.USER_NOT_FOUND, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ResponseCodeEnum.UNKNOWN_SERVER_ERROR));
        }
    }

    @GetMapping("api/donations/amount")
    public ResponseEntity<ApiResponse> getTotalDonationAmount() {
        try {
            double totalDonationAmount = donationService.getTotalDonationAmount();
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("totalAmount", totalDonationAmount);
            return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, responseData));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ResponseCodeEnum.UNKNOWN_SERVER_ERROR));
        }
    }
}
