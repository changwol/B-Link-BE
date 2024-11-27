package com.blink.server.boardComment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardCommentPostDto {
    private String boardCode;
    private String commentContent;
}
