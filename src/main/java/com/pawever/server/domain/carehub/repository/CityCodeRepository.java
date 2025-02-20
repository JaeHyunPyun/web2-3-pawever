package com.pawever.server.domain.carehub.repository;

import com.pawever.server.domain.carehub.entity.CityCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityCodeRepository extends JpaRepository<CityCode, Long> {
}
