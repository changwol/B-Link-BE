package com.blink.server.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberLoginDto {
    private String userId;
    private String userPassWord;
}
