package com.blink.server.boardComment.service;

import com.blink.server.board.repository.BoardRepository;
import com.blink.server.boardComment.dto.BoardCommentPostDto;
import com.blink.server.boardComment.entity.BoardComment;
import com.blink.server.boardComment.repository.BoardCommentRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BoardCommentService {
    private final BoardCommentRepository boardCommentRepository;
    private final BoardRepository boardRepository;

    public Mono<BoardComment> postComment(BoardCommentPostDto dto, String memberId) {
        BoardComment comment = new BoardComment();
        comment.setCommentContent(dto.getCommentContent());
        comment.setCommentPostDateTime(LocalDateTime.now());
        comment.setBoardCode(dto.getBoardCode());
        comment.setMemberId(memberId);
        return boardCommentRepository.save(comment);
    }
}
