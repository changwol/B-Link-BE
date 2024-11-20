package com.blink.server.chat.entity;


import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "room")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class ChatRoom {
    @Id
    private String id;
    private String roomName;
}