package org.example.store.chatRoom;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.store.chat.ChatDto;
import org.example.store.member.dto.MemberDto;
import org.example.store.member.entity.Member;
import org.example.store.product.dto.ProductDto;
import org.example.store.product.entity.Product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@Builder
public class ChatRoomDto {
    private int roomId;

    private MemberDto fromUser;

    private MemberDto toUser;

    private ProductDto productDto;

    private List<ChatDto> chatDtoList;

    private int unreadCount;

    private String latestMessage;

    private LocalDateTime latestMessageTime;

    private String latestTimeStr;

    public static ChatRoom toEntity(ChatRoomDto chatRoomDto) {

        return ChatRoom.builder()
                .product(ProductDto.toEntity(chatRoomDto.getProductDto()))
                .toUser(MemberDto.toEntity(chatRoomDto.getToUser()))
                .fromUser(MemberDto.toEntity(chatRoomDto.getFromUser()))
                .roomId(chatRoomDto.getRoomId())
                .chatList(chatRoomDto.getChatDtoList() == null ? new ArrayList<>() : ChatDto.toEntityList(chatRoomDto.getChatDtoList()))
                .build();
    }

    public static ChatRoomDto fromProjection(ChatRoomProjection projection) {
        ChatRoom chatRoom = projection.getChatRoom();

        return ChatRoomDto.builder()
                .roomId(chatRoom.getRoomId())
                .fromUser(Member.fromEntity(chatRoom.getFromUser()))
                .toUser(Member.fromEntity(chatRoom.getToUser()))
                .productDto(Product.fromEntity(chatRoom.getProduct()))
                .latestMessage(projection.getLatestMessage() != null ? projection.getLatestMessage() : "")
                .latestMessageTime(projection.getLatestMessageTime())
                .build();
    }
}
