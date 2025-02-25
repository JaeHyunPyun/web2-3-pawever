package com.pawever.server.domain.carehub.dto.response;

import lombok.Getter;

@Getter
public class DistrictCodeDTO {
    private String uprCd;     // 상위 시도 코드
    private String orgCd;     // 시군구 코드
    private String orgdownNm; // 시군구 이름
}
