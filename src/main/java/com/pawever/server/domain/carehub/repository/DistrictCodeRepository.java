package com.pawever.server.domain.carehub.repository;

import com.pawever.server.domain.carehub.entity.DistrictCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DistrictCodeRepository extends JpaRepository<DistrictCode, Long> {
    boolean existsByOrgCd(String orgCd); // 중복 저장 방지용

    @Query("select d.orgCd from DistrictCode d where d.name = :districtName and d.uprCd = :cityId")
    Long findCodeByName(@Param("districtName") String districtName, Long cityId);

    @Query("select d.uprCd from DistrictCode d where d.name = :districtName and d.uprCd = :cityId")
    Long findUprCodeByName(@Param("districtName") String districtName, Long cityId);
}
