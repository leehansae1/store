package org.example.store.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.dto.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 방번호로 채팅방 조회, 채팅내역 가져오기 + 읽음 처리
    @PostMapping("/chat/list/{roomId}")
    public Map<String, Object> getList(@PathVariable int roomId,
                                       @AuthenticationPrincipal CustomUserDetails user) {
        List<ChatDto> chatDtoList
                = chatService.getList(user.getLoggedMember(), roomId);

        return chatDtoList != null
                ? Map.of("chatList", chatDtoList, "success", true)
                : Map.of("success", false);
    }
}
