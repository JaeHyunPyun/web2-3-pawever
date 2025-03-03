package com.pawever.server.domain.carehub.repository;

import com.pawever.server.domain.carehub.entity.AbandonedPet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AbandonedPetRepository extends JpaRepository<AbandonedPet, Long> {
    List<AbandonedPet> findByBreedContaining(String breed);

}
