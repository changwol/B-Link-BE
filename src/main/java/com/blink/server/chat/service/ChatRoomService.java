package com.blink.server.chat.service;

import com.blink.server.chat.dto.RoomInfo;
import com.blink.server.chat.entity.ChatRoom;
import com.blink.server.chat.entity.Message;
import com.blink.server.chat.repository.ChatRoomRepository;
import com.blink.server.chat.repository.MessageRepository;
import com.blink.server.member.entity.Member;
import com.blink.server.member.repository.MemberRepository;
import com.blink.server.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    private MessageRepository messageRepository;
    private MemberRepository memberRepository;
    @Autowired
    private SimpMessagingTemplate brokerMessagingTemplate;
    private final MongoTemplate mongoTemplate;
    @Autowired
    private MemberService memberService;

    @Autowired
    public ChatRoomService(MemberRepository memberRepository, ChatRoomRepository chatRoomRepository, MongoTemplate mongoTemplate) {
        this.memberRepository = memberRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public Flux<Message> findChatRoomByRoomId(String roomId) {
        return messageRepository.findByRoomId(roomId) // roomId로 ChatRoom 조회
                .switchIfEmpty(Mono.error(new RuntimeException("ChatRoom not found"))); // 방이 없을 경우 에러 처리
    }

//    public Mono<Void> updateRoomId(String memberId1, String memberId2, String newRoomId) {
//        return memberService.getRoomIds(memberId1)
//                .flatMap(member1RoomIds -> {
//                    if (!member1RoomIds.contains(newRoomId)) {
//                        Criteria criteria = Criteria.where("memberId").is(memberId1);
//                        Query query = new Query(criteria);
//                        Update update = new Update().addToSet("roomIds", newRoomId);
//                        mongoTemplate.updateFirst(query, update, Member.class);
//                        brokerMessagingTemplate.convertAndSendToUser(memberId1, "/topic/room/find", newRoomId);
//                        System.out.println("Member1의 룸 추가");
//                    }
//                    return Mono.empty();
//                })
//                .then(memberService.getRoomIds(memberId2))
//                .flatMap(member2RoomIds -> {
//                    if (!member2RoomIds.contains(newRoomId)) {
//                        Criteria criteria = Criteria.where("memberId").is(memberId2);
//                        Query query = new Query(criteria);
//                        Update update = new Update().addToSet("roomIds", newRoomId);
//                        mongoTemplate.updateFirst(query, update, Member.class);
//                        brokerMessagingTemplate.convertAndSendToUser(memberId2, "/topic/room/find", newRoomId);
//                        System.out.println("Member2의 룸 추가");
//                    }
//                    return Mono.empty();
//                });
//    }

    public Mono<Object> updateRoomId(String memberId1, String memberId2, String newRoomId) {
        return memberService.getRoomIds(memberId1)
                .flatMap(member1RoomIds -> {
                    if (!member1RoomIds.contains(newRoomId)) {
                        Criteria criteria = Criteria.where("memberId").is(memberId1);
                        Query query = new Query(criteria);
                        Update update = new Update().addToSet("roomIds", newRoomId);
                        return Mono.fromRunnable(() -> mongoTemplate.updateFirst(query, update, Member.class))
                                .doOnSuccess(aVoid -> {
                                    // 사용자에게 업데이트된 내용을 전송
                                    brokerMessagingTemplate.convertAndSendToUser(memberId1, "/topic/room/find", newRoomId);
                                    System.out.println("Member1의 룸 추가");
                                });
                    }
                    return Mono.empty();
                })
                .then(memberService.getRoomIds(memberId2))
                .flatMap(member2RoomIds -> {
                    if (!member2RoomIds.contains(newRoomId)) {
                        Criteria criteria = Criteria.where("memberId").is(memberId2);
                        Query query = new Query(criteria);
                        Update update = new Update().addToSet("roomIds", newRoomId);
                        return Mono.fromRunnable(() -> mongoTemplate.updateFirst(query, update, Member.class))
                                .doOnSuccess(aVoid -> {
                                    // 사용자에게 업데이트된 내용을 전송
                                    brokerMessagingTemplate.convertAndSendToUser(memberId2, "/topic/room/find", newRoomId);
                                    System.out.println("Member2의 룸 추가");
                                });
                    }
                    return Mono.empty();
                });
    }


    public Mono<ChatRoom> save(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom); // MongoDB에 저장
    }

    public Mono<List<RoomInfo>> getRoomNamesByMemberId(String memberId) {
        return memberRepository.findByMemberId(memberId) // memberId로 Member 찾기
                .flatMap(member -> {
                    if (member == null) {
                        return Mono.just(Collections.emptyList()); // Member가 없을 경우 빈 리스트 반환
                    }

                    List<String> roomIds = member.getRoomIds(); // Member 객체에서 roomIds 가져오기

                    // 방 ID 리스트가 비어 있는지 확인
                    if (roomIds.isEmpty()) {
                        return Mono.just(Collections.emptyList()); // 빈 리스트 반환
                    }

                    // 각 방 ID에 대해 방 이름을 조회
                    return Flux.fromIterable(roomIds)
                            .flatMap(roomId -> chatRoomRepository.getRoomNameById(roomId) // 방 이름 조회
                                    .map(chatRoom -> new RoomInfo(roomId, chatRoom.getRoomName())) // ChatRoom에서 이름 추출
                                    .defaultIfEmpty(new RoomInfo(roomId, "Unknown Room"))) // 방 이름이 없을 경우 기본값 설정
                            .collectList(); // 결과를 리스트로 수집
                });
    }

    //    public String getRoomId(String roomName) {
//        // 방 이름을 기준으로 쿼리 생성
//        Query query = new Query();
//        query.addCriteria(Criteria.where("roomName").is(roomName));
//
//        // 방 정보를 조회
//        ChatRoom room = mongoTemplate.findOne(query, ChatRoom.class); // Room은 방 정보를 가진 도메인 클래스
//
//        // 방이 존재하지 않으면 null 반환
//        if (room == null) {
//            return null;
//        }
//
//        return room.getId(); // 방 ID 반환
//    }
    public Mono<String> getRoomId(String roomName) {
        // 방 이름을 기준으로 쿼리 생성
        Query query = new Query();
        query.addCriteria(Criteria.where("roomName").is(roomName));

        // 방 정보를 조회
        return Mono.fromCallable(() -> mongoTemplate.findOne(query, ChatRoom.class)) // 비동기적으로 조회
                .map(room -> {
                    if (room == null) {
                        return null; // 방이 없으면 null 반환
                    }
                    return room.getId(); // 방 ID 반환
                });
    }

}
