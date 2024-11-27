package com.blink.server.board.dto;

import com.blink.server.boardComment.dto.BoardCommentResponseDto;
import com.blink.server.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BoardDetailResponseDto {
    private String boardCode;
    private String boardTitle;
    private String boardContent;
    private String boardPostDate;
    private boolean boardIsAnnouncement;
    private int boardView;
    private String memberCode;
    private String memberId;

    private List<BoardCommentResponseDto> commentList;
}
