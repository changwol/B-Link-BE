package com.blink.server.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardDeleteDto {
    private String boardCode;
    private String memberPassword;
}
