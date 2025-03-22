package org.example.store.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.entity.Member;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
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
        // 읽음 처리
        chatRepository.markMessagesAsRead(roomId, member.getUserId());
        // 해당 채팅방의 메시지를 모두 조회
        List<Chat> chatList = chatRepository.findAllByChatRoom_RoomIdOrderByChatId(roomId);

        List<ChatDto> chatDtoList = Chat.fromEntityList(chatList);
        chatDtoList.forEach(chatDto -> {
            chatDto.getWriter().setUserPw("");
            log.info("chatDto == {}", chatDto);
        });
        return chatDtoList;
    }

    public int getUnreadMessageCount(int roomId, String userId) {
        log.info("roomId {}", roomId);
        log.info("userId {}", userId);
        return chatRepository.countUnreadMessages(roomId, userId);
    }
}
