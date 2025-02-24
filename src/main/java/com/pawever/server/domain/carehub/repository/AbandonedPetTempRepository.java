package com.pawever.server.domain.carehub.repository;

import com.pawever.server.domain.carehub.entity.AbandonedPetTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AbandonedPetTempRepository extends JpaRepository<AbandonedPetTemp, Long> {

    @Modifying
    @Query("DELETE FROM AbandonedPetTemp")
    void deleteAllPets();

}
