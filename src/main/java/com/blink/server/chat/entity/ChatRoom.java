package com.blink.server.chat.entity;


import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


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
    private String member1;
    private String member2;
    private LocalDateTime lastchatTime;
}