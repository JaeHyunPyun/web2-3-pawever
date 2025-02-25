package com.pawever.server.domain.donation.repository;

import com.pawever.server.domain.donation.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
}
