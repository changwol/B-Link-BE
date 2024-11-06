package com.blink.server.board.repository;

import com.blink.server.board.entity.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom {
    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Void> increaseView(ObjectId boardCode) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(boardCode));
        Update update = new Update().inc("boardView", 1);  // view 필드를 1 증가시킴

        return mongoTemplate.updateFirst(query, update, Board.class)
                .then(); // 완료 후 Mono<Void> 반환
    }
}
