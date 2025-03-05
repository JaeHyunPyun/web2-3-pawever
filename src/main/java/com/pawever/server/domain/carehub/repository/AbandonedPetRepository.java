package com.pawever.server.domain.carehub.repository;

import com.pawever.server.domain.carehub.entity.AbandonedPet;
import com.pawever.server.domain.carehub.enums.Species;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AbandonedPetRepository extends JpaRepository<AbandonedPet, Long> {

    @Query("SELECT a FROM AbandonedPet a WHERE a.providerShelterId IN :providerShelterId AND a.species = :species")
    Page<AbandonedPet> findByProviderShelterIdsAndSpecies(@Param("providerShelterId") List<Long> providerShelterId,
                                                  @Param("species") Species species,
                                                  Pageable pageable);
}
