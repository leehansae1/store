package org.example.store.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.entity.Member;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatDto write(ChatDto chatDto) {
        //db 입력한 채팅을 화면에도 뿌려주기 위해 출력
        Chat chat = chatRepository.save(ChatDto.toEntity(chatDto));
        return Chat.fromEntity(chat);
    }

    public List<ChatDto> getList(Member member, int roomId) {
        List<Chat> chatList = chatRepository.findAllByChatRoom_RoomId(roomId);
        // 읽음 처리를 위해 다시 저장

        chatList.forEach(chat -> {
            if (!chat.getWriter().getUserId().equals(member.getUserId())) {
                Chat newChat = Chat.builder()
                        .chatId(chat.getChatId()) //업데이트 쿼리가 나갈 수 있도록 아이디저장
                        .isRead(true) //읽음 처리
                        .writer(chat.getWriter())
                        .content(chat.getContent())
                        .chatRoom(chat.getChatRoom())
                        .chatDate(chat.getChatDate())
                        .build();
                chatRepository.save(newChat);
            }
        });
        return Chat.fromEntityList(chatList);
    }

    public List<ChatDto> justCountUnread(int roomId) {
        List<Chat> chatList = chatRepository.findAllByChatRoom_RoomId(roomId);
        return Chat.fromEntityList(chatList);
    }
}
