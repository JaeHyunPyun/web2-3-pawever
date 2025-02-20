package com.pawever.server.domain.carehub.repository;

import com.pawever.server.domain.carehub.entity.DistrictCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistrictCodeRepository extends JpaRepository<DistrictCode, Long> {
    boolean existsByOrgCd(String orgCd); // 중복 저장 방지용
}
