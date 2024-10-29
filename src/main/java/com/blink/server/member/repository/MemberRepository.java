package com.blink.server.member.repository;

import com.blink.server.member.entity.Member;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MemberRepository extends ReactiveCrudRepository<Member, String> {

    Flux<Member> findAll();

    Mono<Member> findByMemberId(String memberId);
}
