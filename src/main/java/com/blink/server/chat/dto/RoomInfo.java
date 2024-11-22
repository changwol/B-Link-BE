package com.blink.server.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomInfo {
    private String Id;
    private String roomName;

    public RoomInfo(String roomId, String roomName) {
        this.Id = roomId;
        this.roomName = roomName;
    }
}
