package com.pawever.server.domain.recommendation.service;

import com.pawever.server.domain.recommendation.dto.recommendation.TraitImpact;
import com.pawever.server.domain.recommendation.entity.QuestionTrait;
import com.pawever.server.domain.recommendation.repository.QuestionTraitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DogQuestionTraitMappingService {

    private final QuestionTraitRepository questionTraitRepository;

    //질문ID와 선택한 옵션 ID에 따른 특성 영향 정보 조회
    public List<TraitImpact> getTraitImpacts(int questionId, int optionId) {
        List<QuestionTrait> traits = questionTraitRepository.findByQuestionIdAndOptionId(questionId, optionId);

        return traits.stream()
                .map(trait -> new TraitImpact(
                        trait.getTraitName(),
                        trait.getScore(),
                        trait.getWeight(),
                        trait.getTolerance(),
                        trait.getReverse()
                ))
                .collect(Collectors.toList());
    }
}