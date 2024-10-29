package com.blink.server.board.repository;

import com.blink.server.board.entity.Board;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface BoardRepository extends ReactiveMongoRepository<Board, String> {

//    @Query("{user.userName :  ?0}")
//    Flux<Board> findByUserName(String username);

//    @Query("{boardTitle : ?0}")
//    Flux<Board> findByBoardTitle(String title);

}
