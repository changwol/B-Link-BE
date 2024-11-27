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

//    @MessageMapping("/user/{memberName}")
//    @SendTo("/sub/user")
//    public Flux<Member> findUserName(@DestinationVariable String memberName, @Payload Map<String, String> payload) {
//        String searchTerm = payload.get("searchTerm");
//        System.out.println("Received payload: " + payload); // payload 내용 확인
//        System.out.println("searchTerm = " + searchTerm);
//
//        if (searchTerm == null) {
//            logger.error("searchTerm이 null입니다.");
//            return Flux.error(new IllegalArgumentException("searchTerm이 null입니다."));
//        }
//        return chatRoomService.getMemberName(searchTerm);
//    }


//    @GetMapping("/find/{memberId}/findRoom") //완성
//    public Mono<List<RoomInfo>> findFirstRoomId(@PathVariable String memberId) {
//        return chatRoomService.getRoomNamesByMemberId(memberId)
//                .doOnNext(roomInfos -> logger.info("Retrieved Room Info for memberId {}: {}", memberId, roomInfos))
//                .doOnError(error -> logger.error("Error occurred while retrieving room info for memberId {}: {}", memberId, error.getMessage()));
//    }

    @GetMapping("/memberId")
    public Mono<ResponseEntity<String>> getMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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

//    @MessageMapping("/create/{memberId1}/{memberId2}/{roomName}")
//    public Mono<ChatRoom> create(@PathVariable String memberId1, @PathVariable String memberId2, @PathVariable String roomName) {
//        ChatRoom chatRoom = new ChatRoom();
//        chatRoom.setRoomName(roomName);
//        System.out.println("roomName = " + roomName);
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
//                    messagingTemplate.convertAndSend("/sub/find", room);
//                })
//                .doOnError(error -> logger.error("Error occurred while creating chat room: {}", error.getMessage()));
//    }

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


//        return chatRoomService.createRoom(room)
//                .doOnNext(createRoom -> {
//                    Mono<String> roomId = chatRoomService.getRoomId(roomName);
//                    System.out.println("roomId = " + roomId);
//                    chatRoomService.updateRoomId(member1, member2, roomId);
//
//                    messagingTemplate.convertAndSend("/sub/find/", createRoom);
//                    messagingTemplate.convertAndSend("/sub/addRoom/" + member1, createRoom);
//                    messagingTemplate.convertAndSend("/sub/addRoom/" + member1, createRoom);
//                })
//                .doOnSuccess(aVoid -> {
//                    // 모든 비동기 작업이 완료된 후 실행
//                    System.out.println("Adding room for member: " + room.getMember1() + " and " + room.getMember2());
//                })
//                .then(); // 최종적으로 Mono<Void> 반환
//}
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

//    @MessageMapping("/create")
//    public Mono<Void> createRoom(@Payload ChatRoomDto room) {
//        System.out.println("room = " + room);
//        return chatRoomService.createRoom(room)
//                .doOnNext(createRoom -> {
//                    messagingTemplate.convertAndSend("/sub/find/", createRoom);
//                    messagingTemplate.convertAndSend("/sub/addRoom/"+room.getMember1(), createRoom);
//                    messagingTemplate.convertAndSend("/sub/addRoom/"+room.getMember2(), createRoom);
//
//                    Mono<String> roomId = chatRoomService.getRoomId(createRoom.getId());
//                    String memberId = room.getMember1Id();
//                    System.out.println("memberId = " + memberId);
//                    memberService.addRoomIdToMember(memberId,roomId.toString());
//
//                    System.out.println("Adding room for member: " + room.getMember1() + " and " + room.getMember2());
//                })
//                .then();
//    }

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

