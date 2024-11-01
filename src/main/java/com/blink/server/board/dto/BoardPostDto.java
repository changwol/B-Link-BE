package com.blink.server.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardPostDto {
    private String boardTitle;
    private String boardContent;
    private String boardPostDate;
    private boolean boardisAnnouncement;
}
