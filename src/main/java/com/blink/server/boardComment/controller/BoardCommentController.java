package com.blink.server.boardComment.controller;

import com.blink.server.boardComment.dto.BoardCommentPostDto;
import com.blink.server.boardComment.entity.BoardComment;
import com.blink.server.boardComment.service.BoardCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@Tag(name = "Comment", description = "댓글 관련 API 입니다.")
@CrossOrigin(origins = {"http://localhost:3000","http://127.0.0.1:3000"})
public class BoardCommentController {
    private final BoardCommentService boardCommentService;

    @PostMapping("/post")
    @Operation(summary = "댓글 작성하기", description = "댓글 작성하는 메서드입니다.")
    public Mono<BoardComment> postComment(@RequestBody BoardCommentPostDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return boardCommentService.postComment(dto, userId);
    }
}
