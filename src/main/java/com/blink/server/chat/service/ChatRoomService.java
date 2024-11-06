package com.blink.server.chat.service;

import com.blink.server.chat.dto.ChatRoomDto;
import com.blink.server.chat.entity.ChatRoom;
//import com.blink.server.chat.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatRoomService {

//    private final RoomRepository roomRepository;
//    private final UserService userService;

//    @Autowired
//    public ChatRoomService(RoomRepository roomRepository, UserService userService) {
//        this.roomRepository = roomRepository;
//        this.userService = userService;
//    }
//
//    @Transactional
//    public ChatRoomDto createChatRoom(UserSingUpDto dto) {
//        ChatRoom chatRoom = new UserSingUpDto(dto.getUserId(),chatRoomDto.getRoomName());
//        Mono<ChatRoom> savedChatRoom = roomRepository.save(chatRoom);
//        return ChatRoomDto.of(savedChatRoom.getUserId(), savedChatRoom.getRoomIds());
//    }

//    public Optional<ChatRoom> getChatRoom(String roomId) {
//        return roomRepository.findByRoomId(roomId); // roomId로 채팅 방 찾기
//    }
}
