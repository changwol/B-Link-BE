package com.blink.server.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ChatRoomDto {

    private String id;
    private String roomName;

    @Builder
    public ChatRoomDto(String id, String roomName) {
        this.id = id;
        this.roomName = roomName;
    }
    public static ChatRoomDto of(UUID id, String roomName) {
        return new ChatRoomDto(id.toString(), roomName);
    }
}
