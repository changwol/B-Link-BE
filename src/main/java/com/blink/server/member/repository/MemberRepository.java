package com.blink.server.member.repository;

import com.blink.server.member.entity.Member;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Repository
@EnableReactiveMongoRepositories
public interface MemberRepository extends ReactiveCrudRepository<Member, String> {

    Flux<Member> findAll();

    Mono<Member> findByMemberId(String memberId);

    Mono<Member> findByRoomIds(String memberId);

    Flux<Member> findByMemberName(String memberId);

    Flux<Member> findByMemberNameContainingIgnoreCase(String memberName);


//    List<String> getRoomIds(String memberId);
}
