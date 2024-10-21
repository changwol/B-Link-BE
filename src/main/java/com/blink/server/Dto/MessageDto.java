package com.blink.server.Dto;

import com.blink.server.Entity.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class MessageDto {

    @Setter
    private String roomId;
    @Setter
    private String id;
    @Setter
    private String senderName;
    @Setter
    private String content;

    public MessageDto(String roomId, String name, String content) {
        this.roomId = roomId;
        this.senderName = name;
        this.content = content;
    }

    public static MessageDto of(Message message) {
        return new MessageDto(
                message.getRoomId(),
                message.getSenderName(),
                message.getContent()
        );
    }
}