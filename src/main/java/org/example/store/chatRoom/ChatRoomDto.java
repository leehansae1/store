package org.example.store.chatRoom;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.store.chat.ChatDto;
import org.example.store.member.dto.MemberDto;
import org.example.store.product.dto.ProductDto;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
public class ChatRoomDto {
    private int roomId;

    private MemberDto fromUser;

    private MemberDto toUser;

    private ProductDto productDto;

    private List<ChatDto> chatDtoList;

    private int unreadCount;

    @Builder
    public ChatRoomDto(int roomId, List<ChatDto> chatDtoList, ProductDto productDto,
                       MemberDto fromUser, MemberDto toUser, int unreadCount) {
        this.roomId = roomId;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.productDto = productDto;
        this.chatDtoList = chatDtoList;
        this.unreadCount = unreadCount;
    }

    public static ChatRoom toEntity(ChatRoomDto chatRoomDto) {

        return ChatRoom.builder()
                .product(ProductDto.toEntity(chatRoomDto.getProductDto()))
                .toUser(MemberDto.toEntity(chatRoomDto.getToUser()))
                .fromUser(MemberDto.toEntity(chatRoomDto.getFromUser()))
                .roomId(chatRoomDto.getRoomId())
                .chatList(new ArrayList<>())
                .build();
    }

    public static List<ChatRoom> toEntityList(List<ChatRoomDto> chatRoomDtos) {
        List<ChatRoom> chatRooms = new ArrayList<>();
        chatRoomDtos.forEach(chatRoomDto ->
                chatRooms.add(toEntity(chatRoomDto))
        );
        return chatRooms;
    }
}
