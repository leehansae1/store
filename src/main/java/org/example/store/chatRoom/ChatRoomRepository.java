package org.example.store.chatRoom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

    // 내가 구매자, 판매자인 채팅방 조회
    @Query(value = "select * from chatroom where FromUSER_ID = :userId or ToUSER_ID = :userId"
            , nativeQuery = true)
    List<ChatRoom> findAllByUserId(String userId);

    // 나와 거래대상의 채팅방 존재여부, 존재한다면 채팅방 가져온다
    @Query(value = "select * from chatroom " +
            "where (fromUser_Id = :fromUserId and toUser_Id = :toUserId) " +
            "or (toUser_Id = :fromUserId and fromUser_Id = :toUserId)"
            , nativeQuery = true)
    ChatRoom findChatRoomBy(String fromUserId, String toUserId);
}
