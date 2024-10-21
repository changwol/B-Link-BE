package com.blink.server.user.repository;

import com.blink.server.user.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    @Query("{userName :  ?0}")
    User findByUsername(String username);

}
