package com.pawever.server.domain.LikedPet.repository;

import com.pawever.server.domain.LikedPet.entity.LikedPet;
import com.pawever.server.domain.LikedPet.entity.LikedPetId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikedPetRepository extends JpaRepository<LikedPet, LikedPetId> {
    List<LikedPet> findByIdUserId(Long userId);

}
