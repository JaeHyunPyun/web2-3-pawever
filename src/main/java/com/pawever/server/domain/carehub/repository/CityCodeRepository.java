package com.pawever.server.domain.carehub.repository;

import com.pawever.server.domain.carehub.entity.CityCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityCodeRepository extends JpaRepository<CityCode, Long> {
}
