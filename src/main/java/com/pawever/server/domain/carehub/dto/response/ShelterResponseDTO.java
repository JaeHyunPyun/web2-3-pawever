package com.pawever.server.domain.carehub.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShelterResponseDTO {
    private Long ShelterId;
    private String ShelterName;
}
