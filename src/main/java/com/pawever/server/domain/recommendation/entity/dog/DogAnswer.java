package com.pawever.server.domain.recommendation.entity.dog;

import com.pawever.server.domain.recommendation.dto.recommendation.Answer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "dog_answer")
public class DogAnswer implements Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "option_id", nullable = false)
    private Integer optionId;

    @Column(name = "option_text", nullable = false, length = 255)
    private String optionText;

}