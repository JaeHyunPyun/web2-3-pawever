package com.pawever.server.domain.donation.repository;

import com.pawever.server.domain.donation.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.pawever.server.domain.user.entity.jpa.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByUserId(User user);

    @Query("SELECT SUM(d.donationAmount) FROM Donation d")
    Optional<Double> calculateTotalAmount();
}
