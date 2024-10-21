package com.blink.server.board.repository;

import com.blink.server.board.entity.Board;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends MongoRepository<Board, String> {

    @Query("{user.userName :  ?0}")
    Board findByUserName(String username);

    @Query("{boardTitle : ?0}")
    Board findByBoardTitle(String title);

}
