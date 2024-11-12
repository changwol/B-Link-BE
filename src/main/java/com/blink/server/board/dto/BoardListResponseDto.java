package com.blink.server.board.dto;

import lombok.*;
import reactor.core.publisher.Flux;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardListResponseDto {
    private Long boardContentCount ; // 총 게시물 수

    List<BoardListInformationDto> data;

}
