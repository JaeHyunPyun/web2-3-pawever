package com.pawever.server.domain.recommendation.service;

import com.pawever.server.common.exception.CustomException;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.recommendation.dto.recommendation.Answer;
import com.pawever.server.domain.recommendation.dto.recommendation.QuestionResponse;
import com.pawever.server.domain.recommendation.entity.cat.CatAnswer;
import com.pawever.server.domain.recommendation.entity.cat.CatQuestion;
import com.pawever.server.domain.recommendation.entity.dog.DogAnswer;
import com.pawever.server.domain.recommendation.entity.dog.DogQuestion;
import com.pawever.server.domain.recommendation.repository.CatAnswerRepository;
import com.pawever.server.domain.recommendation.repository.CatQuestionRepository;
import com.pawever.server.domain.recommendation.repository.DogAnswerRepository;
import com.pawever.server.domain.recommendation.repository.DogQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final DogQuestionRepository dogQuestionRepository;
    private final CatQuestionRepository catQuestionRepository;
    private final DogAnswerRepository dogAnswerRepository;
    private final CatAnswerRepository catAnswerRepository;



    @Transactional(readOnly = true)
    public QuestionResponse getDogQuestion(Long questionId) {
        DogQuestion question = dogQuestionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(ResponseCodeEnum.QUESTION_NOT_FOUND));

        List<DogAnswer> dogAnswers = dogAnswerRepository.findByQuestionIdOrderByOptionId(questionId);

        List<Answer> answers = dogAnswers.stream()
                .map(answer -> (Answer) answer)
                .collect(Collectors.toList());
        return new QuestionResponse(question.getQuestionId(), question.getQuestionText(), answers);

    }

    @Transactional(readOnly = true)
    public QuestionResponse getCatQuestion(Long questionId) {
        CatQuestion question = catQuestionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(ResponseCodeEnum.QUESTION_NOT_FOUND));

        List<CatAnswer> catAnswers = catAnswerRepository.findByQuestionIdOrderByOptionId(questionId);

        List<Answer> answers = catAnswers.stream()
                .map(answer->(Answer)answer)
                .collect(Collectors.toList());

        return new QuestionResponse(question.getQuestionId(), question.getQuestionText(), answers);
    }
}
