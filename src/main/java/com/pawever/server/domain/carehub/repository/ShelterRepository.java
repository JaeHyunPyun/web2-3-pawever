package com.pawever.server.domain.carehub.repository;

import com.pawever.server.domain.carehub.entity.DistrictCode;
import com.pawever.server.domain.carehub.entity.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShelterRepository extends JpaRepository<Shelter, Long> {
}
