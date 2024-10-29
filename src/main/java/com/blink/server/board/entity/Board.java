package com.blink.server.board.entity;

import com.blink.server.member.entity.Member;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "BOARD")
@Builder
public class Board {
    @Id
    private BigInteger boardCode;
    private String boardTitle;
    private String boardContent;
    private String boardPostDate; // yyyymmdd 형식으로 기입
    private boolean boardIsAnnouncement;
    private int boardView; // 조회수

    @DBRef(lazy = true) // 지연방식 연관관계 설정 ( 작성자의 정보 )
    private Member member;

}
