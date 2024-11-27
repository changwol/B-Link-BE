package com.blink.server.board.service;

import com.blink.server.board.dto.*;
import com.blink.server.board.dto.BoardDetailResponseDto;
import com.blink.server.board.entity.Board;
import com.blink.server.board.repository.BoardRepository;
import com.blink.server.boardComment.service.BoardCommentService;
import com.blink.server.member.entity.Member;
import com.blink.server.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final BoardCommentService commentService;

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
        ObjectId objectId = new ObjectId(boardCode);
        boardRepository.deleteByBoardCode(objectId).subscribe();
    }

    /**
     * 게시글 ID 값을 이용해 하나의 게시글 리턴하는 메서드
     * @param boardCode Board ID 값
     * @return BoardEntity -> DTO 로 변환후 리턴
     */
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

                            // 댓글 목록 가져오기
                    return commentService.getBoardCommentListByBoardCode(boardCode)
                            .flatMap(commentList -> {
                            dto.setCommentList(commentList);

                    // board view 증가 후 dto 반환
                    return boardRepository.increaseView(objectId)
                            .then(Mono.just(dto));  // view 증가 후 DTO 반환
                            });
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("작성글이 존재하지 않습니다.")));
    }

    /**
     * 게시글 목록 가져오는 메서드
     * @param page 게시글 페이지. 20 * page
     * @return BoardListResponseDto
     */
    public BoardListResponseDto getBoardList(int page) {
        int pageSize = 20; // 페이지당 항목 수

        // 게시물 목록을 가져오고, 해당 목록의 총 개수를 함께 가져옵니다.
        long count = boardRepository.count().switchIfEmpty(Mono.just(0L)).block();  // .block()을 사용하여 count 값을 동기적으로 가져옵니다.

        // 게시물 목록을 가져와서 DTO로 매핑
        Flux<BoardListInformationDto> boardListFlux = boardRepository.findAll()
                .skip((page-1) * pageSize) // page 번호에 맞춰 건너뛴 후
                .take(pageSize)        // pageSize 만큼 가져옵니다
                .sort((a,b) -> b.getBoardPostDate().compareTo(a.getBoardPostDate())) // 날짜 최신순 정렬
                .map(board -> new BoardListInformationDto(
                        board.getBoardCode(),
                        board.getBoardTitle(),
                        board.getBoardPostDate(),
                        board.getMember().getMemberId(),  // board에서 memberId를 가져옴
                        board.getBoardView()
                ));
        List<BoardListInformationDto> list = boardListFlux.collectList().block();

        // boardListFlux가 Flux<BoardListInformationDto>이므로 이를 Mono로 묶고, count와 함께 반환
        return new BoardListResponseDto(count, list);
    }

    /**
     * 글 수정하는 메서드
     */
    public Mono<String> updateBoard(BoardUpdateDto dto) {
        System.out.println("dto = " + dto);
        Mono<Board> board = boardRepository.findBoardByBoardCode(new ObjectId(dto.getBoardCode()));
        return board.flatMap(
                newBoard -> {
                    newBoard.setBoardTitle(dto.getBoardTitle());
                    newBoard.setBoardContent(dto.getBoardContent());
                    newBoard.setBoardPostDate(LocalDateTime.now().toString());
                    return boardRepository.save(newBoard)
                            .thenReturn("수정이 완료되었습니다.");
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("작성글이 존재하지 않습니다.")));
    }

}
