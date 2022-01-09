package com.jjong0416.jwttutorial.dto;

import lombok.*;

// Token 정보를 Response할 때, 사용할 TokenDto class 생성
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {
    private String token;
}