package com.blink.server.chat.service;

import com.blink.server.chat.dto.ChatRoomDto;
import com.blink.server.chat.entity.ChatRoom;
import com.blink.server.chat.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatRoomService {

    private final RoomRepository roomRepository;

    @Autowired
    public ChatRoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Transactional
    public ChatRoomDto createChatRoom(ChatRoomDto requestChatRoomDto) {
        ChatRoom chatRoom = new ChatRoom(requestChatRoomDto.getRoomName());
        ChatRoom savedChatRoom = roomRepository.save(chatRoom);
        return ChatRoomDto.of(savedChatRoom.getId(), savedChatRoom.getRoomName());
    }

    @Transactional
    public List<ChatRoomDto> findChatRoomList() {
        List<ChatRoom> chatRooms = (List<ChatRoom>) roomRepository.findAll();
        return chatRooms.stream()
                .map(chatRoom -> ChatRoomDto.of(chatRoom.getId(), chatRoom.getRoomName()))
                .collect(Collectors.toList());
    }
}
