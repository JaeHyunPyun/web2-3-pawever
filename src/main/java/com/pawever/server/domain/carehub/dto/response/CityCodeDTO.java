package com.pawever.server.domain.carehub.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CityCodeDTO {

    @JsonProperty("orgCd")
    private String orgCd;

    @JsonProperty("orgdownNm")
    private String orgdownNm;
}
