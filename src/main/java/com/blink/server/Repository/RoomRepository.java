package com.blink.server.Repository;

import com.blink.server.Entity.ChatRoom;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends CrudRepository<ChatRoom, String> {

}
