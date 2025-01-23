package org.example.store.chatRoom;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.store_project.chat.Chat;
import org.example.store_project.chat.ChatDto;
import org.example.store_project.member.MemberDto;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
public class ChatRoomDto {
    private int roomId;

    private MemberDto fromUser;

    private MemberDto toUser;

    private List<ChatDto> chatDtoList;

    private int unreadCount;

    @Builder
    public ChatRoomDto(int roomId, List<ChatDto> chatDtoList,
                       MemberDto fromUser, MemberDto toUser, int unreadCount) {

        this.roomId = roomId;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.chatDtoList = chatDtoList;
        this.unreadCount = unreadCount;
    }

    public static ChatRoom toEntity(ChatRoomDto chatRoomDto) {
        List<Chat> chats = new ArrayList<>();
        List<ChatDto> chatDtos = chatRoomDto.getChatDtoList();
        chatDtos.forEach(chatDto ->
                chats.add(ChatDto.toEntity(chatDto))
        );

        return ChatRoom.builder()
                .toUser(MemberDto.toEntity(chatRoomDto.getToUser()))
                .fromUser(MemberDto.toEntity(chatRoomDto.getFromUser()))
                .roomId(chatRoomDto.getRoomId())
                .chatList(chats)
                .build();
    }

//    public static List<ChatRoom> toEntityList(List<ChatRoomDto> chatRoomDtos) {
//        List<ChatRoom> chatRooms = new ArrayList<>();
//        chatRoomDtos.forEach(chatRoomDto ->
//                chatRooms.add(toEntity(chatRoomDto))
//        );
//        return chatRooms;
//    }
}
