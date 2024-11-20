package com.blink.server.chat.entity;

import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat") // 실제 몽고 DB 컬렉션 이름
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    private String id;
    @Setter
    private String roomId;
    @Setter
    private String content;
    @Setter
    private String senderName;
    @Setter
    private LocalDateTime createdDate;

    public Message(String roomId, String content, String senderName) {
        this.roomId = roomId;
        this.content = content;
        this.senderName = senderName;
        this.createdDate = LocalDateTime.now();
    }
}