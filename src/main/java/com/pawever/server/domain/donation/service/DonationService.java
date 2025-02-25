package com.pawever.server.domain.donation.service;

import com.pawever.server.domain.donation.entity.Donation;
import com.pawever.server.domain.donation.repository.DonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class DonationService {
    @Autowired
    private DonationRepository donationRepository;

    public void createDonation(Map<String, Object> request) {
        Donation donation = new Donation();
        donation.setDonorName((String) request.get("donorName"));
        donation.setDonorMessage((String) request.get("donorMessage"));
        donation.setDonationAmount(((Number) request.get("amount")).longValue());
        donation.setCreatedAt(LocalDateTime.now());
        donation.setUpdatedAt(LocalDateTime.now());

        donationRepository.save(donation);
    }
}
