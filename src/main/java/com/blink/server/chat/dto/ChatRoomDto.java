package com.blink.server.chat.dto;

import com.blink.server.chat.entity.ChatRoom;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@NoArgsConstructor
public class ChatRoomDto {

    private String id;
    private String roomName;
    private String member1;
    private String member2;
    private LocalDateTime lastChatTime;
    @Builder
    public ChatRoomDto(String id, String roomName, String member1, String member2, LocalDateTime  lastChatTime) {
        this.id = id;
        this.roomName = roomName;
        this.member1 = member1;
        this.member2 = member2;
        lastChatTime = lastChatTime;
    }
    public static ChatRoomDto of(ChatRoom chatRoom) {

        return new ChatRoomDto(
                chatRoom.getId(),
                chatRoom.getRoomName(),
                chatRoom.getMember1(),
                chatRoom.getMember2(),
                chatRoom.getLastchatTime()
        );
    }

}
