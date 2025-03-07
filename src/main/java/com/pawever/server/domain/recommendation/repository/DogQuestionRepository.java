package com.pawever.server.domain.recommendation.repository;

import com.pawever.server.domain.recommendation.entity.dog.DogQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DogQuestionRepository extends JpaRepository<DogQuestion, Long> {
}

