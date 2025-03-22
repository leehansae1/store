package org.example.store.chatRoom;

import org.example.store.member.entity.Member;
import org.example.store.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

    // 내가 구매자, 판매자인 채팅방 조회
    @Query("""
    SELECT cr AS chatRoom, 
           (SELECT c.content FROM Chat c 
            WHERE c.chatRoom.roomId = cr.roomId 
            ORDER BY c.chatDate DESC FETCH FIRST 1 ROW ONLY) AS latestMessage,
           COALESCE(
               (SELECT c.chatDate FROM Chat c 
                WHERE c.chatRoom.roomId = cr.roomId 
                ORDER BY c.chatDate DESC FETCH FIRST 1 ROW ONLY),
               TO_TIMESTAMP('', '')
           ) AS latestMessageTime
    FROM ChatRoom cr
    LEFT JOIN FETCH cr.product p
    LEFT JOIN FETCH cr.fromUser
    LEFT JOIN FETCH cr.toUser
    WHERE cr.fromUser.userId = :userId OR cr.toUser.userId = :userId
    ORDER BY latestMessageTime DESC
""")
    List<ChatRoomProjection> findAllChatRoomsWithLatestMessage(@Param("userId") String userId);

    // 상품 아이디로 내가 그 상품을 거래했던 채팅방 찾기 >> 나와 상품이 같은 채팅방은 하나일 수 밖에 없음
    @Query(value = "select * from chatRoom " +
            "where (fromUser_Id = :userId or toUser_Id = :userId) and product_id = :productId", nativeQuery = true)
    ChatRoom findByProductIdAndMember(int productId, String userId);

    @Query(value = "select * from chatRoom where roomId = :roomId", nativeQuery = true)
    ChatRoom findByRoomId(int roomId);
}
