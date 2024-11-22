package com.blink.server.chat.controller;

import com.blink.server.chat.entity.ChatRoom;
import com.blink.server.chat.service.ChatRoomService;
import com.blink.server.member.service.MemberService;
import com.blink.server.chat.dto.RoomInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/room")
public class ChatRoomController {

    private static final Logger logger = LoggerFactory.getLogger(ChatRoomController.class);

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private MemberService memberService;

//    @GetMapping(value = "/find/{memberId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<List<String>> findRoomId(@PathVariable String memberId) {
//        return memberService.getRoomIdsFlux(memberId) // 데이터 변화가 있을 때마다 방 ID를 가져오는 Flux
//                .doOnNext(ids -> logger.info("Retrieved Room IDs for memberId {}: {}", memberId, ids))
//                .doOnError(error -> logger.error("Error occurred while retrieving room IDs for memberId {}: {}", memberId, error.getMessage()));
//    }

    @GetMapping("/memberId")
    public Mono<ResponseEntity<String>> getMemberInfomation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        System.out.println("userId = " + userId);

        return Mono.just(ResponseEntity.ok(userId));
    }

    @GetMapping("/find/{memberId}")
    public Mono<List<RoomInfo>> findRoomId(@PathVariable String memberId) {
        return chatRoomService.getRoomNamesByMemberId(memberId)
                .doOnNext(roomInfos -> logger.info("Retrieved Room Info for memberId {}: {}", memberId, roomInfos))
                .doOnError(error -> logger.error("Error occurred while retrieving room info for memberId {}: {}", memberId, error.getMessage()));
    }


    @PostMapping("/create/{memberId1}/{memberId2}/{roomName}")
    public Mono<ChatRoom> create(@PathVariable String memberId1, @PathVariable String memberId2, @PathVariable String roomName) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRoomName(roomName);

        return chatRoomService.save(chatRoom)
                .flatMap(savedRoom -> {
                    return memberService.addRoomIdToMember(memberId1, savedRoom.getId())
                            .then(memberService.addRoomIdToMember(memberId2, savedRoom.getId()))
                            .then(Mono.just(savedRoom)); // 방 ID 추가 후 저장된 방 반환
                })
                .doOnNext(room -> logger.info("Chat room created: {}", room.getRoomName()))
                .doOnError(error -> logger.error("Error occurred while creating chat room: {}", error.getMessage()));
    }
}
//@GetMapping("/find/{memberId}")
//public Mono<List<String>> findRoomId(@PathVariable String memberId) {
//
//    return memberService.getRoomIds(memberId)
//            .doOnNext(ids -> logger.info("Retrieved Room IDs for memberId {}: {}", memberId, ids))
//            .doOnError(error -> logger.error("Error occurred while retrieving room IDs for memberId {}: {}", memberId, error.getMessage()));
//}

//@GetMapping("/find/{memberId}")
//public Mono<List<String>> findRoomId(@PathVariable String memberId) {
//    Mono<List<String>> roomIds = memberService.getRoomIds(memberId);
//    Mono<List<String>> roomNames = chatRoomService.getRoomNameById(roomIds);
//    return memberService.getRoomIds(memberId)
//            .doOnNext(ids -> logger.info("Retrieved Room IDs for memberId {}: {}", memberId, ids))
//            .doOnError(error -> logger.error("Error occurred while retrieving room IDs for memberId {}: {}", memberId, error.getMessage()));
//}
//@GetMapping("/find/{memberId}")
//public Mono<List<String>> findRoomId(@PathVariable String memberId) {
//    return memberService.getRoomIds(memberId) // memberId로 방 ID 리스트를 가져옴
//            .flatMap(roomIds -> {
//                // 방 ID 리스트를 Flux로 변환하여 각 방 ID에 대해 방 이름을 조회
//                System.out.println(roomIds);
//                return Flux.fromIterable(roomIds) // roomIds가 List<String> 타입이어야 합니다.
//                        .flatMap(Id -> chatRoomService.getRoomNameById(Id) // 방 이름 조회
//                                .defaultIfEmpty("Unknown Room")) // 방 이름이 없을 경우 기본값 설정
//                        .collectList(); // 결과를 리스트로 수집
//            })
//            .doOnNext(roomNames -> logger.info("Retrieved Room Names for memberId {}: {}", memberId, roomNames))
//            .doOnError(error -> logger.error("Error occurred while retrieving room names for memberId {}: {}", memberId, error.getMessage()));
//}
