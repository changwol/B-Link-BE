package com.blink.server.chat.dto;

import com.blink.server.chat.entity.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@NoArgsConstructor
public class MessageDto {

    private String id;
    private String roomId;
    private String senderName;
    private String senderId;
    private String content;

    public MessageDto(String roomId, String name,String senderId, String content) {
        this.roomId = roomId;
        this.senderName = name;
        this.senderId = senderId;
        this.content = content;
    }

    public static MessageDto of(Message message) {
        return new MessageDto(
                message.getRoomId(),
                message.getSenderName(),
                message.getSenderId(),
                message.getContent()
        );
    }
}