package org.example.store.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store_project.member.Member;
import org.example.store_project.member.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    private final MemberRepository memberRepository;

    // 아직 답글처럼 보이기 위한 level step 등은 하지 않음
    public ChatDto write(ChatDto chatDto, String writerId) {
        Member writer; //넘어온 chatDto 에 writer 를 저장을 위함
        Optional<Member> member = memberRepository.findByUserId(writerId);
        if (member.isPresent()) writer = member.get();
        else return null;
        chatDto.setWriter(Member.fromEntity(writer));
        //db 입력한 채팅을 화면에도 뿌려주기 위해 출력
        Chat chat = chatRepository.save(ChatDto.toEntity(chatDto));
        return Chat.fromEntity(chat);
    }

    @Transactional
    public List<ChatDto> getList(int roomId) {
        List<Chat> chatList = chatRepository.findAllByChatRoom_RoomId(roomId);
        // 읽음처리
        chatList.forEach(chat -> {
            Chat newChat = Chat.builder()
                    .chatId(chat.getChatId()) //업데이트 쿼리가 나갈 수 있도록 아이디저장,,
                    .isRead(true) //읽음 처리
                    .writer(chat.getWriter())
                    .content(chat.getContent())
                    .chatRoom(chat.getChatRoom())
                    .chatDate(chat.getChatDate())
                    .build();
            chatRepository.save(newChat); // 될까..
        });
        return Chat.fromEntityList(chatList);
    }
}
