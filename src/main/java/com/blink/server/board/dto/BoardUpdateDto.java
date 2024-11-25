package com.blink.server.board.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BoardUpdateDto {
    private String boardCode;
    private String boardTitle;
    private String boardContent;
}
