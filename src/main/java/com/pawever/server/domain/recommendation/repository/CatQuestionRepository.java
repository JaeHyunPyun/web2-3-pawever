package com.pawever.server.domain.recommendation.repository;

import com.pawever.server.domain.recommendation.entity.cat.CatQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatQuestionRepository extends JpaRepository<CatQuestion, Long> {
}
