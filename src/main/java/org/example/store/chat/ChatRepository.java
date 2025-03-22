package org.example.store.chat;

import org.example.store.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    // 채팅방 번호로 조회하는 채팅내역
    List<Chat> findAllByChatRoom_RoomIdOrderByChatId(int chatRoomRoomId);

    // 읽지 않은 메시지 읽음 처리
    @Modifying
    @Transactional
    @Query("UPDATE Chat c SET c.isRead = true WHERE c.chatRoom.roomId = :roomId AND c.writer.userId != :userId")
    void markMessagesAsRead(@Param("roomId") int roomId, @Param("userId") String userId);

    @Query("SELECT COUNT(c) FROM Chat c WHERE c.chatRoom.roomId = :roomId AND c.isRead = false AND c.writer.userId != :userId")
    int countUnreadMessages(@Param("roomId") int roomId, @Param("userId") String userId);
}
