package com.blink.server.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class JwToken {
    private String grantType;
    private String accessToken;
    private String refreshToken;

}
