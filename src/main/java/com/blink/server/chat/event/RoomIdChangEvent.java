package com.blink.server.chat.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomIdChangEvent {
        private String memberId; // 멤버 ID
        private String roomId;    // 방 ID

        public RoomIdChangEvent(String memberId, String roomId) {
            this.memberId = memberId;
            this.roomId = roomId;
        }
}
