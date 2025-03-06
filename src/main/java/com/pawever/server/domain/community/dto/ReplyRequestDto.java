package com.pawever.server.domain.community.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplyRequestDto {
    @NotBlank(message = "내용은 필수 입력값입니다.")
    private String content;

}
