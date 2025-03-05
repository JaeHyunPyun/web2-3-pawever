package com.pawever.server.domain.carehub.repository;

import com.pawever.server.domain.carehub.entity.CityCode;
import com.pawever.server.domain.carehub.entity.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CityCodeRepository extends JpaRepository<CityCode, Long> {

    @Query("select c.orgCd from CityCode c where c.name = :cityName")
    Long findCodeByName(@Param("cityName") String cityName);
}
