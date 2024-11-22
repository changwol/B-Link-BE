package com.blink.server.chat.repository;

import com.blink.server.chat.entity.ChatRoom;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ChatRoomRepository extends ReactiveCrudRepository<ChatRoom, String> {
    Mono<ChatRoom> findById(String roomId);
    Mono<ChatRoom> save(ChatRoom chatRoom);
    Mono<ChatRoom> getRoomNameById(String roomId);
}
