package com.blink.server.board.repository;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface BoardRepositoryCustom {
    Mono<Void> increaseView(ObjectId boardCode); // view를 증가시키는 메서드
}
