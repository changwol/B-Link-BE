package com.blink.server.chat.controller;

import com.blink.server.chat.entity.ChatRoom;
import com.blink.server.chat.entity.CreateChatRequest;
import com.blink.server.chat.service.ChatRoomService;
import com.blink.server.member.service.MemberService;
import com.blink.server.chat.dto.RoomInfo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/room")
@CrossOrigin(origins = {"http://localhost:3000","http://127.0.0.1:3000"})
public class ChatRoomController {

    private static final Logger logger = LoggerFactory.getLogger(ChatRoomController.class);

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private MemberService memberService;

    private final SimpMessagingTemplate messagingTemplate;

    public ChatRoomController(ChatRoomService chatRoomService, MemberService memberService, SimpMessagingTemplate messagingTemplate) {
        this.chatRoomService = chatRoomService;
        this.memberService = memberService;
        this.messagingTemplate = messagingTemplate; // 메시징 템플릿 주입
    }

//    @MessageMapping("/find/{memberId}")
//    @SendTo("/sub/find")
//    public Mono<List<RoomInfo>> findRoomId(@DestinationVariable String memberId, Principal principal) {
//        logger.info("findRoomId 호출됨, memberId: {}", memberId); // 로그 추가
//        return chatRoomService.getRoomNamesByMemberId(memberId)
//                .doOnNext(roomInfos -> logger.info("Retrieved Room Info for memberId {}: {}", memberId, roomInfos))
//                .doOnError(error -> {
//                    logger.error("Error occurred while retrieving room info for memberId {}: {}", memberId, error.getMessage());
//                })
//                .defaultIfEmpty(Collections.emptyList());
//    }



    @GetMapping("/find/{memberId}") //완성
    public Mono<List<RoomInfo>> findFirstRoomId(@PathVariable String memberId) {
        return chatRoomService.getRoomNamesByMemberId(memberId)
                .doOnNext(roomInfos -> logger.info("Retrieved Room Info for memberId {}: {}", memberId, roomInfos))
                .doOnError(error -> logger.error("Error occurred while retrieving room info for memberId {}: {}", memberId, error.getMessage()));
    }

    @GetMapping("/memberId")
    public Mono<ResponseEntity<String>> getMemberInfomation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        System.out.println("userId = " + userId);

        return Mono.just(ResponseEntity.ok(userId));
    }

//    @MessageMapping("/create")
//    public Mono<ChatRoom> create(@RequestBody CreateChatRequest request) {
//        System.out.println("roomName = " + request);
//
//        String memberId1 = request.getMemberId1();
//        String memberId2 = request.getMemberId2();
//        String roomName = request.getRoomName();
//
//        ChatRoom chatRoom = new ChatRoom();
//        chatRoom.setRoomName(roomName);
//        System.out.println("roomName = " + roomName);
//
//        return chatRoomService.save(chatRoom)
//                .flatMap(savedRoom -> {
//                    return memberService.addRoomIdToMember(memberId1, savedRoom.getId())
//                            .then(memberService.addRoomIdToMember(memberId2, savedRoom.getId()))
//                            .then(Mono.just(savedRoom)); // 방 ID 추가 후 저장된 방 반환
//                })
//                .flatMap(savedRoom -> {
//                    return chatRoomService.updateRoomId(memberId1, memberId2, savedRoom.getId())
//                            .then(Mono.just(savedRoom)); // 업데이트 후 방 반환
//                })
//                .doOnNext(room -> {
//                    logger.info("Chat room created: {}", room.getRoomName());
//                    // 방 생성 후 사용자에게 방 정보 전송
//                    messagingTemplate.convertAndSend("/topic/find", room);
//                })
//                .doOnError(error -> logger.error("Error occurred while creating chat room: {}", error.getMessage()));
//    }

    @MessageMapping("/create/{memberId1}/{memberId2}/{roomName}")
    public Mono<ChatRoom> create(@PathVariable String memberId1, @PathVariable String memberId2, @PathVariable String roomName) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRoomName(roomName);
        System.out.println("roomName = " + roomName);
        return chatRoomService.save(chatRoom)
                .flatMap(savedRoom -> {
                    return memberService.addRoomIdToMember(memberId1, savedRoom.getId())
                            .then(memberService.addRoomIdToMember(memberId2, savedRoom.getId()))
                            .then(Mono.just(savedRoom)); // 방 ID 추가 후 저장된 방 반환
                })
                .flatMap(savedRoom -> {
                    return chatRoomService.updateRoomId(memberId1, memberId2, savedRoom.getId())
                            .then(Mono.just(savedRoom)); // 업데이트 후 방 반환
                })
                .doOnNext(room -> {
                    logger.info("Chat room created: {}", room.getRoomName());
                    // 방 생성 후 사용자에게 방 정보 전송
                    messagingTemplate.convertAndSend("/sub/find", room);
                })
                .doOnError(error -> logger.error("Error occurred while creating chat room: {}", error.getMessage()));
    }
}

//@PostMapping("/create/{memberId1}/{memberId2}/{roomName}")
//public Mono<ChatRoom> create(@PathVariable String memberId1, @PathVariable String memberId2, @PathVariable String roomName) {
//    ChatRoom chatRoom = new ChatRoom();
//    chatRoom.setRoomName(roomName);
//    String roomId = chatRoomService.getRoomId(roomName);
//    Mono<List<String>> member1Room = memberService.getRoomIds(memberId1);
//    return chatRoomService.save(chatRoom)
//            .flatMap(savedRoom -> {
//                return memberService.addRoomIdToMember(memberId1, savedRoom.getId())
//                        .then(memberService.addRoomIdToMember(memberId2, savedRoom.getId()))
//                        .then(Mono.just(savedRoom)); // 방 ID 추가 후 저장된 방 반환
//            })
//            .doOnNext(room->chatRoomService.updateRoomId(memberId1,memberId2,roomId))
//            .doOnNext(room -> logger.info("Chat room created: {}", room.getRoomName()))
//            .doOnError(error -> logger.error("Error occurred while creating chat room: {}", error.getMessage()));
//}