package com.blink.server.chat.service;

import com.blink.server.chat.dto.MessageDto;
import com.blink.server.chat.entity.Message;
import com.blink.server.chat.repository.MessageRepository;
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

    public Mono<Message> saveChatMessage(MessageDto chat) {
        Message message = new Message();
        message.setRoomId(chat.getRoomId());
        message.setContent(chat.getContent());
        message.setSenderName(chat.getSenderName());
        message.setSenderId(chat.getSenderId());
        message.setCreatedDate(LocalDateTime.now());
        return messageRepository.save(message);//몽고 저장
    }

}
