package com.pawever.server.domain.recommendation.repository;

import com.pawever.server.domain.recommendation.entity.cat.CatTraits;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatTraitsRepository extends JpaRepository<CatTraits, Long> {
}
