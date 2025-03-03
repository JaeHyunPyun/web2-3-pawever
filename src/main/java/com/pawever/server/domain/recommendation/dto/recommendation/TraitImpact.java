package com.pawever.server.domain.recommendation.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class TraitImpact {
    private final String traitName;
    private final double score;
    private final double weight;
    private final boolean tolerance;
    private final boolean reverse;

    public TraitImpact(String traitName, double score, double weight) {
        this(traitName, score, weight, false, false);
    }

    public TraitImpact(String traitName, double score, double weight, boolean tolerance) {
        this(traitName, score, weight, tolerance, false);
    }

}