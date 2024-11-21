package com.blink.server.chat.service;

import com.blink.server.chat.entity.ChatRoom;
import com.blink.server.chat.entity.Message;
import com.blink.server.chat.repository.ChatRoomRepository;
import com.blink.server.chat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    private MessageRepository messageRepository;

    public Flux<Message> findChatRoomByRoomId(Mono<String> roomId) {
        return messageRepository.findByRoomId(roomId) // roomId로 ChatRoom 조회
                .switchIfEmpty(Mono.error(new RuntimeException("ChatRoom not found"))); // 방이 없을 경우 에러 처리
    }

    public Mono<ChatRoom> save(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom); // MongoDB에 저장
    }
}
