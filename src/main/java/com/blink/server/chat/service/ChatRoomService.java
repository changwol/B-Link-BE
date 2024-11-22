package com.blink.server.chat.service;

import com.blink.server.chat.dto.RoomInfo;
import com.blink.server.chat.entity.ChatRoom;
import com.blink.server.chat.entity.Message;
import com.blink.server.chat.repository.ChatRoomRepository;
import com.blink.server.chat.repository.MessageRepository;
import com.blink.server.member.entity.Member;
import com.blink.server.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ChatRoomService(MemberRepository memberRepository, ChatRoomRepository chatRoomRepository) {
        this.memberRepository = memberRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    public Flux<Message> findChatRoomByRoomId(Mono<String> roomId) {
        return messageRepository.findByRoomId(roomId) // roomId로 ChatRoom 조회
                .switchIfEmpty(Mono.error(new RuntimeException("ChatRoom not found"))); // 방이 없을 경우 에러 처리
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


}
//    public Mono<String> getRoomNameById(String id) {
//        // 데이터베이스에서 ChatRoom 객체를 조회하고 방 이름을 반환
//        return chatRoomRepository.findById(id)
//                .map(ChatRoom::getRoomName) // ChatRoom 객체에서 방 이름을 추출
//                .doOnNext(roomName -> {
//                    // 방 이름을 콘솔에 출력
//                    System.out.println("Retrieved room name: " + roomName);
//                })
//                .defaultIfEmpty(null); // 방이 없을 경우 null 반환
//    }
//    public Mono<List<String>> getRoomIds(String memberId) {
//        return memberRepository.findByMemberId(memberId) // memberId로 Member 찾기
//                .map(Member::getRoomIds) // Member 객체에서 roomIds 가져오기
//                .defaultIfEmpty(Collections.emptyList()); // Member가 없을 경우 빈 리스트 반환
//    }
//}
