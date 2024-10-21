package com.blink.server.Service;

import com.blink.server.Dto.MessageDto;
import com.blink.server.Entity.Message;
import com.blink.server.Repository.MessageRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Transactional
    public Mono<Message> saveChatMessage(MessageDto chat) {
        Message message = new Message();
        message.setRoomId(chat.getRoomId());
        message.setContent(chat.getContent());
        message.setSenderName(chat.getSenderName());
        message.setCreatedDate(LocalDateTime.now());
        return messageRepository.save(message);//몽고 저장
    }
}
