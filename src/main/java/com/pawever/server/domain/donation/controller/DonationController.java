package com.pawever.server.domain.donation.controller;

import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.donation.dto.DonationTO;
import com.pawever.server.domain.donation.service.DonationService;
import com.pawever.server.domain.user.dto.response.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "후원 API", description = "후원 생성 및 후원 조회 관련 API")
public class DonationController {
    @Autowired
    private DonationService donationService;

    @PostMapping("api/donations")
    @Operation(summary = "후원 생성 API")
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
    @Operation(summary = "관리자용 전체 후원 조회 API")
    public ResponseEntity<ApiResponse> getAllDonations() {
        try {
            List<DonationTO> donations = donationService.getAllDonations();
            return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, donations));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.fail(ResponseCodeEnum.UNKNOWN_SERVER_ERROR));
        }
    }

    @GetMapping("api/users/donations")
    @Operation(summary = "사용자 후원 내역 조회 API")
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
    @Operation(summary = "전체 후원 금액 조회 API")
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
