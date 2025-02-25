package com.pawever.server.domain.donation.controller;

import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.donation.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DonationController {
    @Autowired
    private DonationService donationService;

    @PostMapping("api/donations")
    public ResponseEntity<ApiResponse>  createDonation(@RequestBody Map<String, Object> request) {
        donationService.createDonation(request);
        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS));
    }
}
