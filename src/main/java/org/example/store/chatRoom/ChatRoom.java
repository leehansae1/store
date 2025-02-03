package org.example.store.chatRoom;

import jakarta.persistence.*;
import lombok.*;
import org.example.store.chat.Chat;
import org.example.store.chat.ChatDto;
import org.example.store.member.Member;
import org.example.store.product.entity.Product;

import java.util.ArrayList;
import java.util.List;

// chatRoom 통해 채팅리스트를 가져오기
@Entity
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int roomId; //방번호

    @ManyToOne
    @JoinColumn(name = "FROMUSER_ID")
    private Member fromUser; //채팅을 건 사람

    @ManyToOne
    @JoinColumn(name = "TOUSER_ID")
    private Member toUser; //채팅을 받은 사람

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<Chat> chatList;

    @Builder
    public ChatRoom(int roomId, List<Chat> chatList,
                    Member fromUser, Member toUser, Product product) {
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
