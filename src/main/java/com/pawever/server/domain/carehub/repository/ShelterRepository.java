package com.pawever.server.domain.carehub.repository;

import com.pawever.server.domain.carehub.entity.DistrictCode;
import com.pawever.server.domain.carehub.entity.Shelter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter, Long> {
    Optional<Shelter> findByNameAndCityCodeAndDistrictCode(String name, String cityCode, String districtCode);

    Shelter findByProviderShelterId(Long providerShelterId);

    @Query("SELECT s FROM Shelter s WHERE " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(s.latitude)) * " +
            "cos(radians(s.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
            "sin(radians(s.latitude)))) <= :distance")
    List<Shelter> findNearbyShelters(@Param("latitude") BigDecimal latitude,
                                     @Param("longitude") BigDecimal longitude,
                                     @Param("distance") Double distance);

    Page<Shelter> findAll(Pageable pageable);

}
