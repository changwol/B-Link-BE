package com.blink.server.chat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Table(name = "chat_room")
@NoArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "room_name")
    private String roomName; // 대문자로 시작하는 변수명

    public ChatRoom(String roomName) {
        this.roomName = roomName;
    }
}
