package com.blink.server.board.repository;

import com.blink.server.board.entity.Board;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface BoardRepository extends ReactiveMongoRepository<Board, String> , BoardRepositoryCustom{

    @Query(value = "{ '_id' : ?0 }", delete = true)
    Mono<Void> deleteByBoardCode(ObjectId boardCode);

    @Query("{ '_id' : ?0 }")
    Mono<Board> findBoardByBoardCode(ObjectId boardCode);


//    @Query("{user.userName :  ?0}")
//    Flux<Board> findByUserName(String username);

//    @Query("{boardTitle : ?0}")
//    Flux<Board> findByBoardTitle(String title);

}
