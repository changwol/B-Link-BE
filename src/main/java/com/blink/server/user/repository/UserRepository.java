package com.blink.server.user.repository;

import com.blink.server.user.entity.User;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, String> {

    @Query(value = "{ 'userId': ?0, 'userPassWord': ?1 }")
    Optional<User> findUser(String userId, String userPassWord);

    Flux<User> findAll();


    Mono<User> findByUserName(String userName);
    Flux<User> findByUserNameContaining(String userName);

    Optional<User> findByUserId(String userId);
}
