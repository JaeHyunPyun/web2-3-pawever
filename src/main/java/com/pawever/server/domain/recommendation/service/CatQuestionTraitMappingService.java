package com.pawever.server.domain.recommendation.service;

import com.pawever.server.domain.recommendation.dto.recommendation.TraitImpact;
import com.pawever.server.domain.recommendation.entity.CatQuestionTrait;
import com.pawever.server.domain.recommendation.repository.CatQuestionTraitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatQuestionTraitMappingService {

    private final CatQuestionTraitRepository catQuestionTraitRepository;

    public List<TraitImpact> getTraitImpacts(int questionId, int optionId) {
        List<CatQuestionTrait> traits = catQuestionTraitRepository.findByQuestionIdAndOptionId(questionId, optionId);

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

