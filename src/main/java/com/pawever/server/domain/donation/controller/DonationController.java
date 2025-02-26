package com.pawever.server.domain.donation.controller;

import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.donation.dto.DonationTO;
import com.pawever.server.domain.donation.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class DonationController {
    @Autowired
    private DonationService donationService;

    @PostMapping("api/donations")
    public ResponseEntity<ApiResponse> createDonation(@RequestBody Map<String, Object> request) {
        donationService.createDonation(request);
        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS));
    }

    @GetMapping("api/users/staff/donations")
    public ResponseEntity<ApiResponse> getAllDonations() {
        List<DonationTO> donations = donationService.getAllDonations();
        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, donations));
    }

    @GetMapping("api/users/donations/{user_id}")
    public ResponseEntity<ApiResponse> getDonationByUser(@PathVariable Long user_id) {
        List<DonationTO> donationsByUser = donationService.getDonationByUser(user_id);
        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, donationsByUser));
    }
}
