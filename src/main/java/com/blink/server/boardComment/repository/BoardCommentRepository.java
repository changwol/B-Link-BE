package com.blink.server.boardComment.repository;

import com.blink.server.boardComment.entity.BoardComment;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
@EnableReactiveMongoRepositories
public interface BoardCommentRepository extends ReactiveCrudRepository<BoardComment, String> {
    Flux<BoardComment> findAllByBoardCode(String boardCode);
}
