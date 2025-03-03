package com.pawever.server.domain.carehub.repository;

import com.pawever.server.domain.carehub.entity.DistrictCode;
import com.pawever.server.domain.carehub.entity.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter, Long> {
    Optional<Shelter> findByNameAndCityCodeAndDistrictCode(String name, String cityCode, String districtCode);
    List<Shelter> findByProviderShelterIdIn(List<Long> shelterProviderIds);
}
