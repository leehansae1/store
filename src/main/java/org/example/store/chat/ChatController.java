package org.example.store.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.dto.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 방번호로 채팅방 조회, 채팅내역 가져오기 + 읽음 처리
    @PostMapping("/chat/list/{roomId}")
    public Map<String, Object> getList(@PathVariable String roomId,
                                       @AuthenticationPrincipal CustomUserDetails user) {
        log.info("roomId {}", roomId);
        log.info("user {}", user.getLoggedMember().getUserId());
        List<ChatDto> chatDtoList = chatService.getList(user.getLoggedMember(), Integer.parseInt(roomId));

        if (chatDtoList != null) return Map.of("chatList", chatDtoList, "success", true);
        else return Map.of("success", false);
    }
}
