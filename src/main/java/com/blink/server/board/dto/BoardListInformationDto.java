package com.blink.server.board.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardListInformationDto {
    private String boardCode;
    private String boardTitle;
    private String boardPostDate;
    private String boardAuthor;
    private int boardView;

}
