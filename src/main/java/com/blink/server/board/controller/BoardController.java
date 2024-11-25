package com.blink.server.board.controller;

import com.blink.server.board.dto.*;
import com.blink.server.board.dto.BoardDeleteDto;
import com.blink.server.board.dto.BoardDetailResponseDto;
import com.blink.server.board.dto.BoardPostDto;
import com.blink.server.board.service.BoardService;
import com.blink.server.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
@Tag(name = "board", description = "게시판 관련 API")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class BoardController {
    private final BoardService boardService;
    private final MemberService memberService;

    /**
     * 글 작성하는 메서드
     *
     * @param dto 글제목, 글내용, 작성날짜 등의 정보가 담긴 DTO
     */
    @PostMapping("/content")
    @Operation(summary = "글 작성하기", description = "JWT를 이용해 회원 ID를 추출, 게시판에 글을 작성합니다")
    public Mono<ResponseEntity<String>> postBoard(@RequestBody BoardPostDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();  // 캐스팅 없이 getName 사용 가능

        return boardService.postBoard(dto, userId)
                .then(Mono.just(ResponseEntity.ok().body("글 작성이 완료되었습니다.")))
                .doOnError(error -> System.out.println("글 작성 중 오류 발생: " + error.getMessage()));
    }

    @DeleteMapping("/content")
    @Operation(summary = "글 삭제하기", description = "게시글 삭제하는 메서드입니다. JWT, BoardCode 와 유저 비밀번호가 필요합니다.")
    public Mono<ResponseEntity<String>> deleteBoard(@RequestBody BoardDeleteDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        String userPassword = dto.getMemberPassword();

        return memberService.isThisPasswordMatch(userId, userPassword)
                .flatMap(isMatch -> {
                    if (isMatch) {
                        boardService.deleteBoard(dto.getBoardCode());
                        return Mono.just(ResponseEntity.ok("글이 성공적으로 삭제되었습니다."));
                    } else {
                        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("비밀번호가 일치하지 않습니다."));
                    }
                });
    }

    @GetMapping("/content/{id}")
    @Operation(summary = "글 조회하기", description = "게시글을 조회하는 메서드입니다. JWT 가 필요하지 않습니다.")
    public Mono<ResponseEntity<BoardDetailResponseDto>> getBoard(@PathVariable("id") String id) {
        return boardService.getBoardResponseDto(id)
                .map(boardDetailResponseDto -> ResponseEntity.ok(boardDetailResponseDto)) // 200 OK와 함께 DTO 반환
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build())); // 내용이 없으면 404 반환

    }

    @GetMapping("/content/list")
    @Operation(summary = "게시글 목록보기", description = "게시글 목록을 불러오는 메서드입니다. 파라미터로 몇번째 page 를 조회할건지 보내야합니다.")
    public Mono<ResponseEntity<BoardListResponseDto>> getBoardContentList(@RequestParam int page) {
        return Mono.just(ResponseEntity.ok(boardService.getBoardList(page)));
    }

    @PutMapping("/update")
    @Operation(summary = "게시글 수정하기", description = "게시글 수정하는 메서드입니다.")
    public ResponseEntity<Mono<String>> updateBoard(@RequestBody BoardUpdateDto dto) {
        return ResponseEntity.ok(boardService.updateBoard(dto));

    }
}