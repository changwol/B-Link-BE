package com.blink.server.chat.controller;

import com.blink.server.chat.dto.ChatRoomDto;
import com.blink.server.chat.dto.RoomInfo;
import com.blink.server.chat.entity.ChatRoom;
import com.blink.server.chat.service.ChatRoomService;
import com.blink.server.member.entity.Member;
import com.blink.server.member.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/room")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
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

    @MessageMapping("/find/{memberId}")
    @SendTo("/sub/find")
    public Mono<List<RoomInfo>> findRoomId(@DestinationVariable String memberId, Principal principal) {
        logger.info("findRoomId 호출됨, memberId: {}", memberId); // 로그 추가
        return chatRoomService.getRoomNamesByMemberId(memberId)
                .doOnNext(roomInfos -> logger.info("Retrieved Room Info for memberId {}: {}", memberId, roomInfos))
                .doOnError(error -> {
                    logger.error("Error occurred while retrieving room info for memberId {}: {}", memberId, error.getMessage());
                })
                .defaultIfEmpty(Collections.emptyList());
    }

    @GetMapping("/find/{memberId}/findRoom")
    public Mono<List<RoomInfo>> findFirstRoomId(@PathVariable String memberId) {
        logger.info("findRoomId 호출됨, memberId: {}", memberId); // 로그 추가
        return chatRoomService.getRoomNamesByMemberId(memberId)
                .doOnNext(roomInfos -> logger.info("Retrieved Room Info for memberId {}: {}", memberId, roomInfos))
                .doOnError(error -> {
                    logger.error("Error occurred while retrieving room info for memberId {}: {}", memberId, error.getMessage());
                })
                .defaultIfEmpty(Collections.emptyList());
    }

    @GetMapping("/user/{memberName}")
    public Mono<List<Member>> findUserName(@PathVariable String memberName) {
        System.out.println("memberName = " + memberName);
        Mono<List<Member>> tmp = memberService.getMemberNameList(memberName).collectList();
        System.out.println("tmp = " + tmp);
        return memberService.getMemberNameList(memberName).collectList();
    }
    @GetMapping("/memberId")
    public Mono<ResponseEntity<String>> getMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("authentication = " + authentication);
        String userId = authentication.getName();
        System.out.println("userId = " + userId);

        return Mono.just(ResponseEntity.ok(userId));
    }

    @GetMapping("/memberName")
    public Mono<String> getMemberName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        System.out.println("userId = " + userId);

        return memberService.findMemberById(userId);
    }
    @MessageMapping("/create")
    public Mono<Void> createRoom(@Payload ChatRoomDto room) {
        String roomName = room.getRoomName();
        String member1 = room.getMember1Id();
        String member2 = room.getMember2Id();

        System.out.println("roomName = " + roomName);
        System.out.println("member1 = " + member1);
        System.out.println("member2 = " + member2);

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRoomName(roomName);
        chatRoom.setMember1Id(member1);
        chatRoom.setMember2Id(member2);
        chatRoom.setMember1(member1);
        chatRoom.setMember2(member2);
        // ChatRoom 객체를 생성하고, 방을 저장합니다.
        return chatRoomService.save(chatRoom)
                .flatMap(savedRoom -> {
                    // 방 ID를 저장합니다.
                    String roomId = savedRoom.getId();

                    // 두 멤버에게 방 ID를 추가합니다.
                    return memberService.addRoomIdToMember(member1, Mono.just(roomId))
                            .then(memberService.addRoomIdToMember(member2, Mono.just(roomId)))
                            .then(Mono.just(savedRoom)); // 저장된 방 반환
                })
                .doOnNext(savedRoom -> {
                    chatRoomService.updateRoomId(member1, member2, savedRoom.getId());
                    messagingTemplate.convertAndSend("/sub/find/", savedRoom);
                    messagingTemplate.convertAndSend("/sub/addRoom/"+member1, savedRoom);
                    messagingTemplate.convertAndSend("/sub/addRoom/"+room.getMember2(), savedRoom);

                })
                .then(); // 최종적으로 Mono<Void> 반환
    }
    @MessageMapping("/addRoom/{memberId}")
    @SendTo("/sub/addRoom/{memberId}")
    public Mono<ChatRoomDto> addRoom(@Payload ChatRoomDto room) {
        System.out.println("Adding room for member: " + room.getMember1() + " and " + room.getMember2());
        return handleAddRoom(room);
    }

    private Mono<ChatRoomDto> handleAddRoom(ChatRoomDto room) {
        return chatRoomService.createRoom(room)
                .map(createRoom -> room.of(createRoom))
                .doOnNext(createRoom -> {
                    messagingTemplate.convertAndSend("/sub/addRoom/" + room.getMember1(), createRoom);
                    messagingTemplate.convertAndSend("/sub/addRoom/" + room.getMember2(), createRoom);
                });
    }
}

