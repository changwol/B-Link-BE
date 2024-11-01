package com.blink.server.board.service;

import com.blink.server.board.dto.BoardPostDto;
import com.blink.server.board.entity.Board;
import com.blink.server.board.repository.BoardRepository;
import com.blink.server.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    /**
     * 글 작성하는 메서드
     *
     * @param dto    글 제목, 글 내용, 작성 날짜 등 데이터담는 DTO
     * @param userId token 에서 추출한 userId값
     */
    public Mono<Integer> postBoard(BoardPostDto dto, String userId) {
        return memberRepository.findByMemberId(userId)
                .flatMap(member -> {
                    return boardRepository.save(Board.postDtoToEntity(dto, member))
                            .thenReturn(1); // 저장 후 성공 시 1 반환
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("존재하지 않는 사용자입니다.")));
    }

}
