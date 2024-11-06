package com.blink.server.board.service;

import com.blink.server.board.dto.BoardDetailResponseDto;
import com.blink.server.board.dto.BoardPostDto;
import com.blink.server.board.entity.Board;
import com.blink.server.board.repository.BoardRepository;
import com.blink.server.member.entity.Member;
import com.blink.server.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
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

    public void deleteBoard(String boardCode) {
        System.out.println("boardCode = " + boardCode);
        ObjectId objectId = new ObjectId(boardCode);
        boardRepository.deleteByBoardCode(objectId).subscribe();
    }

    public Mono<BoardDetailResponseDto> getBoardResponseDto(String boardCode) {
        ObjectId objectId = new ObjectId(boardCode);
        return boardRepository.findBoardByBoardCode(objectId)
                .flatMap(board -> {
                    // 조회된 board 데이터를 기반으로 dto 필드를 설정
                    BoardDetailResponseDto dto = new BoardDetailResponseDto();
                    dto.setBoardCode(board.getBoardCode());
                    dto.setBoardTitle(board.getBoardTitle());
                    dto.setBoardContent(board.getBoardContent());
                    dto.setBoardPostDate(board.getBoardPostDate());
                    dto.setBoardIsAnnouncement(board.isBoardIsAnnouncement());

                    Member member = board.getMember();
                    dto.setMemberCode(member.getMemberCode());
                    dto.setMemberId(member.getMemberId());

                    dto.setBoardView(board.getBoardView() + 1);

                    // board view 증가 후 dto 반환
                    return boardRepository.increaseView(objectId)
                            .then(Mono.just(dto));  // view 증가 후 DTO 반환
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("작성글이 존재하지 않습니다.")));
    }
}
