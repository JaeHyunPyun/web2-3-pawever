package com.pawever.server.domain.donation.service;

import com.pawever.server.domain.donation.dto.DonationTO;
import com.pawever.server.domain.donation.entity.Donation;
import com.pawever.server.domain.donation.repository.DonationRepository;
import com.pawever.server.domain.user.entity.jpa.User;
import com.pawever.server.domain.user.repository.jpa.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Long createDonation(Map<String, Object> request, String uuid) {
        try {
            if (!request.containsKey("donationAmount") || ((Number) request.get("donationAmount")).longValue() < 0L) { // 요청 값의 donationAmount 값 누락 여부 검사
                throw new IllegalArgumentException("Invalid donationAmount format");
            }

            Donation donation = new Donation();
            User user = userRepository.findUuid(uuid)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user UUID: " + uuid));
            donation.setUserId(user);
            donation.setDonorName((String) request.get("donorName"));

            donation.setDonorMessage((String) request.get("donorMessage"));
            donation.setDonationAmount(((Number) request.get("donationAmount")).longValue());
            donation.setCreatedAt(LocalDateTime.now());
            donation.setUpdatedAt(LocalDateTime.now());
            donationRepository.save(donation);
            return donation.getDonationId();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } catch(Exception e) {
            throw new RuntimeException("Error saving donation", e);
        }
    }

    public List<DonationTO> getAllDonations() {
        List<Donation> donations = donationRepository.findAll();
        return getDonationTO(donations);
    }

    public List<DonationTO> getDonationByUser(String uuid) {
        User user = userRepository.findUuid(uuid)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Donation> donations = donationRepository.findByUserId(user);
        return getDonationTO(donations);

    }

    public List<DonationTO> getDonationTO(List<Donation> donations) {
        return donations.stream().map(donation -> {
            DonationTO donationTO = new DonationTO();
            donationTO.setUserId(donation.getUserId().getSocialLoginUuid());
            donationTO.setDonationId(donation.getDonationId());
            donationTO.setDonorName(donation.getDonorName());
            donationTO.setDonorMessage(donation.getDonorMessage());
            donationTO.setDonationAmount(donation.getDonationAmount());
            donationTO.setCreatedAt(donation.getCreatedAt());
            return donationTO;
        }).collect(Collectors.toList());
    }

    public double getTotalDonationAmount() {
        return donationRepository.calculateTotalAmount().orElse(0.0);
    }

}
