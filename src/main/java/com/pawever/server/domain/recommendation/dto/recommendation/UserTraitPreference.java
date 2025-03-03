package com.pawever.server.domain.recommendation.dto.recommendation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserTraitPreference {
    private final double score;
    private final boolean tolerance;
}
