package com.blink.server.boardComment.service;

import com.blink.server.board.repository.BoardRepository;
import com.blink.server.boardComment.dto.BoardCommentPostDto;
import com.blink.server.boardComment.dto.BoardCommentResponseDto;
import com.blink.server.boardComment.entity.BoardComment;
import com.blink.server.boardComment.repository.BoardCommentRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardCommentService {
    private final BoardCommentRepository boardCommentRepository;

    public Mono<BoardComment> postComment(BoardCommentPostDto dto, String memberId) {
        BoardComment comment = new BoardComment();
        comment.setCommentContent(dto.getCommentContent());
        comment.setCommentPostDateTime(LocalDateTime.now());
        comment.setBoardCode(dto.getBoardCode());
        comment.setMemberId(memberId);
        return boardCommentRepository.save(comment);
    }

    public Mono<List<BoardCommentResponseDto>> getBoardCommentListByBoardCode(String boardCode) {
        return boardCommentRepository.findAllByBoardCode(boardCode)
                .map(BoardCommentResponseDto::toDto) // 댓글 데이터를 DTO로 변환
                .collectList(); // Flux를 List로 변환
    }


}
