package com.pawever.server.domain.carehub.repository;

import com.pawever.server.domain.carehub.entity.AbandonedPet;
import com.pawever.server.domain.carehub.entity.Shelter;
import com.pawever.server.domain.carehub.enums.Species;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AbandonedPetRepository extends JpaRepository<AbandonedPet, Long>, JpaSpecificationExecutor<AbandonedPet> {

    @Query("SELECT a FROM AbandonedPet a WHERE a.providerShelterId IN :providerShelterId AND a.species = :species")
    Page<AbandonedPet> findByProviderShelterIdsAndSpecies(@Param("providerShelterId") List<Long> providerShelterId,
                                                  @Param("species") Species species,
                                                  Pageable pageable);


    @Query("SELECT a.id FROM AbandonedPet a")
    List<Long> findAllIds();


    @Query("SELECT s.name FROM Shelter s WHERE s.sido IS NOT NULL AND s.cityCode = :cityCodeId AND s.districtCode = :districtCodeId")
    List<String> findByCityCodeAndDistrictCode(@Param("cityCodeId") Long cityCodeId,
                                                @Param("districtCodeId") Long districtCodeId);

    List<AbandonedPet> findAllByProviderShelterId(Long providerShelterId);

}
