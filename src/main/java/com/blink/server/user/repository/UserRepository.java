package com.blink.server.user.repository;

import com.blink.server.user.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, String> {

    Flux<User> findAll();
}
