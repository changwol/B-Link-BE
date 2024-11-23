package com.blink.server.chat.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateChatRequest {
    private String memberId1;
    private String memberId2;
    private String roomName;
}
