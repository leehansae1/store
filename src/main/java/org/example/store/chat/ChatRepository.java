package org.example.store.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    // 채팅방 번호로 조회하는 채팅내역
    List<Chat> findAllByChatRoom_RoomId(int chatRoomId);
}
