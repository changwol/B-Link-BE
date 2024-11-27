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
    private String member1Id;
    private String member2;
    private String member2Id;
    private LocalDateTime lastChatTime;
    @Builder
    public ChatRoomDto(String id,
                       String roomName,
                       String member1,
                       String member1Id,
                       String member2,
                       String member2Id,
                       LocalDateTime  lastChatTime) {
        this.id = id;
        this.roomName = roomName;
        this.member1 = member1;
        this.member1Id = member1Id;
        this.member2 = member2;
        this.member2Id = member2Id;
        this.lastChatTime = lastChatTime;
    }
    public static ChatRoomDto of(ChatRoom chatRoom) {

        return new ChatRoomDto(
                chatRoom.getId(),
                chatRoom.getRoomName(),
                chatRoom.getMember1(),
                chatRoom.getMember1Id(),
                chatRoom.getMember2(),
                chatRoom.getMember2Id(),
                chatRoom.getLastchatTime()
        );
    }

}
