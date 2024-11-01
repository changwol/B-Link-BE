package com.blink.server.board.controller;

import com.blink.server.board.dto.BoardPostDto;
import com.blink.server.board.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
@Tag(name ="board",description = "게시판 관련 API")
public class BoardController {
    private final BoardService boardService;

    /**
     * 글 작성하는 메서드
     * @param dto 글제목, 글내용, 작성날짜 등의 정보가 담긴 DTO
     */
    @PostMapping("/content")
    @Operation(summary = "글 작성하기",description = "JWT를 이용해 회원 ID를 추출, 게시판에 글을 작성합니다")
    public Mono<ResponseEntity<String>> postBoard(@RequestBody BoardPostDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();  // 캐스팅 없이 getName 사용 가능

        return boardService.postBoard(dto, userId)
                .then(Mono.just(ResponseEntity.ok().body("글 작성이 완료되었습니다.")))
                .doOnError(error -> System.out.println("글 작성 중 오류 발생: " + error.getMessage()));
    }
}