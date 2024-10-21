package com.blink.server.Controller;

import com.blink.server.Dto.ChatRoomDto;
import com.blink.server.Dto.MessageDto;
import com.blink.server.Entity.Message;
import com.blink.server.Repository.MessageRepository;
import com.blink.server.Repository.RoomRepository;
import com.blink.server.Service.ChatRoomService;
import com.blink.server.Service.MessageService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    private final SimpMessageSendingOperations template;
    private final MessageService messageService;
    private final ChatRoomService chatRoomService;
    private final RoomRepository roomRepository;
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
        return messageService.saveChatMessage(messageDto)
                .map(savedMessage -> MessageDto.of(savedMessage))
                .doOnNext(savedMessageDto -> {
                    template.convertAndSend("/sub/rooms/" + messageDto.getRoomId(), savedMessageDto);
                })
                .then();
    }//클라송신

    @GetMapping("/roomList")
    public ResponseEntity<List<ChatRoomDto>> getChatRoomList() {
        List<ChatRoomDto> responses = chatRoomService.findChatRoomList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/findRoom")
    public Mono<ResponseEntity<List<Message>>> findRoom(@RequestParam("roomId") String roomId) {
        return messageRepository.findByRoomId(roomId)
                .collectList()
                .map(messages -> messages.isEmpty()
                        ? ResponseEntity.notFound().build()
                        : ResponseEntity.ok(messages));
    }

    @GetMapping("/rooms/{roomId}/findMessage")
    public Mono<ResponseEntity<?>> getMessagesByRoomId(@PathVariable String roomId) {
        return messageRepository.findByRoomId(roomId)
                .map(MessageDto::of)
                .collectList()
                .map(messages -> messages.isEmpty()//null?
                        ? ResponseEntity.notFound().build()//404
                        : ResponseEntity.ok(messages))//200
                .defaultIfEmpty(ResponseEntity.notFound().build()); // 예외 처리
    }//지금까지 저장된 데이터
}
