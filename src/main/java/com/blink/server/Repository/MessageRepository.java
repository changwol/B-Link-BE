package com.blink.server.Repository;

import com.blink.server.Entity.Message;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface MessageRepository extends ReactiveCrudRepository<Message, Long> {
    Flux<Message> findBySenderName(String senderName);
    Flux<Message> findByRoomId(String roomId);
}
