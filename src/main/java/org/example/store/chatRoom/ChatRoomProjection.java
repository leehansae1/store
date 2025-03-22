package org.example.store.chatRoom;

import java.time.LocalDateTime;

public interface ChatRoomProjection {
    ChatRoom getChatRoom(); // 채팅방 엔티티
    String getLatestMessage(); // 최신 메시지 내용
    LocalDateTime getLatestMessageTime(); // 최신 메시지 시간
}
