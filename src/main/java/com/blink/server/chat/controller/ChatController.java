package com.blink.server.chat.controller;

import com.blink.server.chat.dto.MessageDto;
import com.blink.server.chat.repository.ChatRoomRepository;
import com.blink.server.chat.repository.MessageRepository;
//import com.blink.server.chat.repository.RoomRepository;
import com.blink.server.chat.service.ChatRoomService;
import com.blink.server.chat.service.MessageService;

import com.blink.server.member.repository.MemberRepository;
import com.blink.server.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000","http://127.0.0.1:3000"})
public class ChatController {

    private final SimpMessageSendingOperations template;
    private final MessageService messageService;
    private final ChatRoomService chatRoomService;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;

    @MessageMapping("/message/rooms/{roomId}")
    @SendTo("/sub/rooms/{roomId}")
    public Mono<MessageDto> receiveMessage(@Payload MessageDto chat) {
        return handleMessage(chat);
    }//클라수신

    private Mono<MessageDto> handleMessage(MessageDto messageDto) {
        return messageService.saveChatMessage(messageDto)
                .map(savedMessage -> MessageDto.of(savedMessage))
                .doOnNext(savedMessageDto -> {
                    template.convertAndSend("/sub/rooms/" + messageDto.getRoomId(), savedMessageDto);
                });
    }

    @MessageMapping("/message")
    public Mono<Void> sendMessage(@Payload MessageDto messageDto) {
        System.out.println("messageDto = " + messageDto.getSenderId());
        return messageService.saveChatMessage(messageDto)
                .map(savedMessage -> MessageDto.of(savedMessage))
                .doOnNext(savedMessageDto -> {
                    template.convertAndSend("/sub/rooms/" + messageDto.getRoomId(), savedMessageDto);
                })
                .then();
    }//클라송신

    @GetMapping("/rooms/{roomId}/findMessage")
    public Mono<ResponseEntity<?>> getMessagesByRoomId(@PathVariable String roomId) {
        return messageRepository.findByRoomId(roomId)
                .map(MessageDto::of)
                .collectList()
                .map(messages -> messages.isEmpty()//null?
                        ? ResponseEntity.notFound().build()//404
                        : ResponseEntity.ok(messages))//200
                .defaultIfEmpty(ResponseEntity.notFound().build()); // 예외 처리
    }
}