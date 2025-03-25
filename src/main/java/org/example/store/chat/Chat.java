package org.example.store.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.store.chatRoom.ChatRoom;
import org.example.store.member.entity.Member;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int chatId;

    private String content;

    private String productImgUrl;

    @CreatedDate
    private LocalDateTime chatDate;

    @ManyToOne
    @JoinColumn(name = "writer_id")
    private Member writer;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    private boolean isRead;

    @Builder
    public Chat(int chatId, String content, LocalDateTime chatDate, boolean isRead,
                Member writer, ChatRoom chatRoom, String productImgUrl) {
        this.productImgUrl = productImgUrl;
        this.isRead = isRead;
        this.chatId = chatId;
        this.content = content;
        this.chatDate = chatDate;
        this.writer = writer;
        this.chatRoom = chatRoom;
    }

    public static ChatDto fromEntity(Chat chat) {
        return ChatDto.builder()
                .isRead(chat.isRead())
                .chatId(chat.getChatId())
                .content(chat.getContent())
                .writer(Member.fromEntity(chat.getWriter()))
                .chatRoomDto(ChatRoom.fromEntity(chat.getChatRoom()))
                .chatDate(chat.getChatDate())
                .productImgUrl(chat.getProductImgUrl())
                .build();
    }
    public static List<ChatDto> fromEntityList(List<Chat> chatList) {
        List<ChatDto> chatDtoList = new ArrayList<>();
        chatList.forEach(chat -> chatDtoList.add(fromEntity(chat)));
        return chatDtoList;
    }
}








