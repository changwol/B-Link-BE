package com.blink.server.boardComment.dto;

import com.blink.server.boardComment.entity.BoardComment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardCommentResponseDto {
    private String commentCode;
    private String commentContent;
    private String memberId;
    private LocalDateTime postDateTime;

    public static BoardCommentResponseDto toDto(BoardComment comment) {
        BoardCommentResponseDto dto = new BoardCommentResponseDto();
        dto.setCommentCode(comment.getCommentCode());
        dto.setCommentContent(comment.getCommentContent());
        dto.setMemberId(comment.getMemberId());
        dto.setPostDateTime(comment.getCommentPostDateTime());

        return dto;
    }
}
