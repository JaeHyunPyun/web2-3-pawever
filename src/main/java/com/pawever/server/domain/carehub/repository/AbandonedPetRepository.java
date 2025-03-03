package com.pawever.server.domain.carehub.repository;

import com.pawever.server.domain.carehub.entity.AbandonedPet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AbandonedPetRepository extends JpaRepository<AbandonedPet, Long> {
    List<AbandonedPet> findByBreedContaining(String breed);

}
