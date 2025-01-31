package org.example.store_project.chatRoom;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.store_project.chat.Chat;
import org.example.store_project.chat.ChatDto;
import org.example.store_project.member.Member;
import org.example.store_project.product.entity.Product;

import java.util.ArrayList;
import java.util.List;

// chatRoom 통해 채팅리스트를 가져오기
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int roomId;

    @ManyToOne
    @JoinColumn(name = "FROMUSER_ID")
    private Member fromUser;

    @ManyToOne
    @JoinColumn(name = "TOUSER_ID")
    private Member toUser;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany
    private List<Chat> chatList;

    @Builder
    public ChatRoom(int roomId, List<Chat> chatList, Member fromUser, Member toUser, Product product) {
        this.roomId = roomId;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.product = product;
        this.chatList = chatList;
    }

    public static ChatRoomDto fromEntity(ChatRoom chatRoom) {
        List<ChatDto> chatDtos = Chat.fromEntityList(chatRoom.getChatList());

        return ChatRoomDto.builder()
                .productDto(Product.fromEntity(chatRoom.getProduct()))
                .roomId(chatRoom.getRoomId())
                .toUser(Member.fromEntity(chatRoom.getToUser()))
                .fromUser(Member.fromEntity(chatRoom.getFromUser()))
                .chatDtoList(chatDtos)
                .build();
    }
    public static List<ChatRoomDto> fromEntityList(List<ChatRoom> chatRooms) {
        List<ChatRoomDto> chatRoomDtos = new ArrayList<>();
        chatRooms.forEach(chatRoom -> chatRoomDtos.add(fromEntity(chatRoom)));
        return chatRoomDtos;
    }
}
