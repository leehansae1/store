package org.example.store.chat;

import lombok.*;
import org.example.store.chatRoom.ChatRoomDto;
import org.example.store.member.dto.MemberDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Setter
public class ChatDto {

    private int chatId;

    private String content;

    private LocalDateTime chatDate;

    private boolean isRead;

    private MemberDto writer;

    private ChatRoomDto chatRoomDto;

    @Builder
    public ChatDto(int chatId, String content, LocalDateTime chatDate,
                   MemberDto writer, boolean isRead, ChatRoomDto chatRoomDto) {
        this.isRead = isRead;
        this.chatId = chatId;
        this.content = content;
        this.chatDate = chatDate;
        this.writer = writer;
        this.chatRoomDto = chatRoomDto;
    }

    public static Chat toEntity(ChatDto chatDto) {
        return Chat.builder()
                .chatId(chatDto.getChatId())
                .content(chatDto.getContent())
                .chatDate(chatDto.getChatDate())
                .writer(MemberDto.toEntity(chatDto.getWriter()))
                .chatRoom(ChatRoomDto.toEntity(chatDto.getChatRoomDto()))
                .isRead(chatDto.isRead())
                .build();
    }

    public static List<Chat> toEntityList(List<ChatDto> chatDtoList) {
        List<Chat> chatList = new ArrayList<>();
        chatDtoList.forEach(chatDto ->
                chatList.add(toEntity(chatDto))
        );
        return chatList;
    }
}
