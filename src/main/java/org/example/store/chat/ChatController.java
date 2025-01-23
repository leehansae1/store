package org.example.store.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController //ajax 처리 >> 추후 합칠 때 apiController 에 같이 넣어준다
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/chat/write/{writerId}")
    public Map<String, Object> write(@RequestBody ChatDto chatDto, @PathVariable String writerId) {
        Map<String, Object> result = new HashMap<>();
        ChatDto resultDto = chatService.write(chatDto, writerId);
        if (resultDto != null) {
            result.put("chat", resultDto);
            result.put("success", true);
        } else result.put("success", false);
        return result;
    }

    // 방번호로 채팅방 조회, 채팅내역 가져오기 + 읽음 처리
    @PostMapping("/chat/list/{roomId}")
    public Map<String, Object> getList(@PathVariable int roomId) {
        Map<String, Object> result = new HashMap<>();
        List<ChatDto> chatDtoList = chatService.getList(roomId);
        if (chatDtoList != null) {
            result.put("chatList", chatDtoList);
            result.put("success", true);
        } else result.put("success", false);
        return result;
    }
}
