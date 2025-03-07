package com.pawever.server.domain.LikedPet.repository;

import com.pawever.server.domain.LikedPet.entity.LikedPet;
import com.pawever.server.domain.LikedPet.entity.LikedPetId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LikedPetRepository extends JpaRepository<LikedPet, LikedPetId> {
    List<LikedPet> findByIdUserId(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM LikedPet l WHERE l.id.abandonedPetId NOT IN :existingIds")
    int deleteByAbandonedPetIdNotIn(@Param("existingIds") List<Long> existingIds);
}
