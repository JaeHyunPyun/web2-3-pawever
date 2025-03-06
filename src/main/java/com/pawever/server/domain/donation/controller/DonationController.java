package com.pawever.server.domain.donation.controller;

import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.donation.dto.DonationTO;
import com.pawever.server.domain.donation.service.DonationService;
import com.pawever.server.domain.user.dto.response.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DonationController {
    @Autowired
    private DonationService donationService;

    @PostMapping("api/donations")
    public ResponseEntity<ApiResponse> createDonation(@RequestBody Map<String, Object> request,
                                                      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        try {
            String uuid = customUserDetails.getUsername();
            long donationId = donationService.createDonation(request, uuid);
            return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, donationId));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Invalid user ID") || e.getMessage().contains("Provided donorName")) {
                return ResponseEntity.badRequest().body(ApiResponse.fail(ResponseCodeEnum.USER_NOT_FOUND));
            }
            return ResponseEntity.badRequest().body(ApiResponse.fail(ResponseCodeEnum.INVALID_REQUEST_ARGUMENT));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.fail(ResponseCodeEnum.UNKNOWN_SERVER_ERROR));
        }
    }

    @GetMapping("admin/donations")
    public ResponseEntity<ApiResponse> getAllDonations() {
        try {
            List<DonationTO> donations = donationService.getAllDonations();
            return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, donations));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.fail(ResponseCodeEnum.UNKNOWN_SERVER_ERROR));
        }
    }

    @GetMapping("api/users/donations")
    public ResponseEntity<ApiResponse> getDonationByUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        try {
            String uuid = customUserDetails.getUsername();
            List<DonationTO> donationsByUser = donationService.getDonationByUser(uuid);
            return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, donationsByUser));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(ApiResponse.fail(ResponseCodeEnum.USER_NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.fail(ResponseCodeEnum.UNKNOWN_SERVER_ERROR));
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
            return ResponseEntity.internalServerError().body(ApiResponse.fail(ResponseCodeEnum.UNKNOWN_SERVER_ERROR));
        }
    }
}
