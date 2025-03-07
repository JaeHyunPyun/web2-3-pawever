package com.pawever.server.domain.recommendation.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class QuestionResponse {
    private Long questionId;
    private String questionText;
    private List<Answer> answers;
}

