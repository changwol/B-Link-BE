package com.blink.server.board.entity;

import com.blink.server.board.dto.BoardPostDto;
import com.blink.server.member.entity.Member;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "board")
@Builder
public class Board{
    @Id
    private String boardCode;
    private String boardTitle;
    private String boardContent;
    private String boardPostDate; // yyyymmdd 형식으로 기입
    private boolean boardIsAnnouncement;
    private int boardView; // 조회수

    private Member member;

    static public Board postDtoToEntity(BoardPostDto dto,Member member) {
        Board boardEntity = new Board();
        boardEntity.setBoardTitle(dto.getBoardTitle());
        boardEntity.setBoardContent(dto.getBoardContent());
        boardEntity.setBoardPostDate(LocalDateTime.now().toString());
        boardEntity.setBoardIsAnnouncement(dto.isBoardisAnnouncement());
        boardEntity.setBoardView(0);
        boardEntity.setMember(member);
        return boardEntity;
    }
}
