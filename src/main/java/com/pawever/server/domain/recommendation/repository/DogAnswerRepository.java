package com.pawever.server.domain.recommendation.repository;

import com.pawever.server.domain.recommendation.entity.dog.DogAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DogAnswerRepository extends JpaRepository<DogAnswer, Long> {
    List<DogAnswer> findByQuestionIdOrderByOptionId(Long questionId);
}

