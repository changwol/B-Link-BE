package com.blink.server.chat.entity;

import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat") // 실제 몽고 DB 컬렉션 이름
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    private String id;
    private String roomId;
    private String content;
    private String senderName;
    private String senderId;
    private LocalDateTime createdDate;

    public Message(String roomId, String content, String senderName, String senderId) {
        this.roomId = roomId;
        this.content = content;
        this.senderName = senderName;
        this.senderId = senderId;
        this.createdDate = LocalDateTime.now();
    }
}