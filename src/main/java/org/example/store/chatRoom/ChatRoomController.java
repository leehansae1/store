package org.example.store_project.chatRoom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 구매자가 판매자에게 채팅 걸 때, 방이 있으면 채팅내역을 가져온다 <> 없으면 방을 만들어준다
    @GetMapping("/chatRoom/{fromUserId}/{productId}")
    // fromUserId 는 나중에 ATHENTI 어쩌구로 바꿀거임
    // productId 는 해당상품, 판매자 조회 시 필요
    public String getChatRoomList(@PathVariable String fromUserId, @PathVariable int productId,
                                  Model model) {
        List<ChatRoomDto> resultDtoList
                = chatRoomService.getChatRoomListAndExist(fromUserId, productId);
        if (resultDtoList != null) {
            model.addAttribute("chatRoomList", resultDtoList.getFirst());
            if (resultDtoList.size() > 1) {
                model.addAttribute("chatList", resultDtoList.getLast());
            }
        }
        return "/chatRoom";
    }

    // 내 채팅방 == chatRoomList 바로 조회 할 때
    @GetMapping("/chatRoom/{fromUserId}")
    // fromUserId 는 나중에 ATHENTI 어쩌구로 바꿀거임
    public String getChatRoomList(Model model, @PathVariable String fromUserId) {
        List<ChatRoomDto> chatRoomDtoList
                = chatRoomService.getChatRoomList(fromUserId);
        // 채팅방 list 없으면 자바스크립트로 아예 접근 막거나 채팅이없다 이런 메시지를 채팅방 대신 띄우기
        model.addAttribute("chatRoomDtoList", chatRoomDtoList);
        return "/chatRoom";
    }
}
