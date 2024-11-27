package com.blink.server.boardComment.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BoardCommentPostDto {
    private String boardCode;
    private String commentContent;
}
