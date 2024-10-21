package com.blink.server.chat.repository;

import com.blink.server.chat.entity.ChatRoom;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends CrudRepository<ChatRoom, String> {

}
