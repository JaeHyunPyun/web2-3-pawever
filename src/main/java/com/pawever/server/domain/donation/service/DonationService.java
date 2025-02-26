package com.pawever.server.domain.donation.service;

import com.pawever.server.domain.donation.dto.DonationTO;
import com.pawever.server.domain.donation.entity.Donation;
import com.pawever.server.domain.donation.repository.DonationRepository;
import com.pawever.server.domain.user.entity.User;
import com.pawever.server.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DonationService {
    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private UserRepository userRepository;

    public void createDonation(Map<String, Object> request) {
        Long userId = ((Number) request.get("userId")).longValue();
        System.out.println("userId: "+ userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));

        Donation donation = new Donation();
        donation.setUserId(user);
        donation.setDonorName((String) request.get("donorName"));
        donation.setDonorMessage((String) request.get("donorMessage"));
        donation.setDonationAmount(((Number) request.get("donationAmount")).longValue());
        donation.setCreatedAt(LocalDateTime.now());
        donation.setUpdatedAt(LocalDateTime.now());

        donationRepository.save(donation);
    }

    public List<DonationTO> getAllDonations() {
        List<Donation> donations = donationRepository.findAll();
        return getDonationTO(donations);
    }

    public List<DonationTO> getDonationByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Donation> donations = donationRepository.findByUserId(user);
        return getDonationTO(donations);

    }

    public List<DonationTO> getDonationTO(List<Donation> donations) {
        return donations.stream().map(donation -> {
            DonationTO donationTO = new DonationTO();
            donationTO.setUserId(donation.getUserId().getUserId());
            donationTO.setDonorName(donation.getDonorName());
            donationTO.setDonorMessage(donation.getDonorMessage());
            donationTO.setDonationAmount(donation.getDonationAmount());
            donationTO.setCreatedAt(donation.getCreatedAt());
            return donationTO;
        }).collect(Collectors.toList());
    }

    public double getTotalDonationAmount() {
        return donationRepository.calculateTotalAmount()
                .orElse(0.0);
    }

}
