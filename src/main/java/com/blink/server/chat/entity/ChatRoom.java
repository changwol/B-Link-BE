package com.blink.server.chat.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "chat_room")
@NoArgsConstructor
public class ChatRoom {

    @Id
    private String id;

    @Column(name = "room_name")
    private String roomName; // 대문자로 시작하는 변수명

    public ChatRoom(String id, String roomName) {
        this.id = id;
        this.roomName = roomName;
    }
}
