package com.pawever.server.domain.donation.repository;

import com.pawever.server.domain.donation.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.pawever.server.domain.user.entity.User;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByUserId(User user);
}
