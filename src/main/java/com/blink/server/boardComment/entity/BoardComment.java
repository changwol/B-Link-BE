package com.blink.server.boardComment.entity;

import com.blink.server.board.entity.Board;
import com.blink.server.boardComment.dto.BoardCommentPostDto;
import com.blink.server.member.entity.Member;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "boardComment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardComment {
    @Id
    private String commentCode;

    private LocalDateTime commentPostDateTime;
    private String commentContent;
    private String memberId;
    private String boardCode;
}
