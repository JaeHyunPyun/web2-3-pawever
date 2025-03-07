package com.pawever.server.domain.recommendation.repository;

import com.pawever.server.domain.recommendation.entity.cat.CatAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CatAnswerRepository extends JpaRepository<CatAnswer, Long> {
    List<CatAnswer> findByQuestionIdOrderByOptionId(Long questionId);
}

