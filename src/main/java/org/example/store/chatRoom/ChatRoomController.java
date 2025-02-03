package org.example.store.chatRoom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.chat.ChatDto;
import org.example.store.member.entity.Member;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 최초 채팅
    // 구매자가 판매자에게 채팅 걸 때, 방이 있으면 채팅내역을 가져온다 <> 없으면 방을 만들어준다
    @GetMapping("/chatRoom/{productId}")
    // productId 는 해당상품, 판매자 조회 시 필요
    public String getChatRoomList(@PathVariable int productId, Model model) {
        Member 내계정 = null; // 내계정 >> ATHENTI 어쩌구로 바꿀거임
        List<ChatRoomDto> resultDtoList
                = chatRoomService.getChatRoomListAndExist(내계정, productId);

        if (resultDtoList != null) {
            model.addAttribute("chatRoomList", resultDtoList.getFirst());
            if (resultDtoList.size() > 1) {
                model.addAttribute("chatList", resultDtoList.getLast());
            }
        }
        return "/chatRoom";
    }

    // 내 기존 채팅방 조회 >> 없으면 문구하나 정도 띄우기, 아니면 모달로 알려주기?
    @GetMapping("/chatRoom")
    // 내계정 >> 나중에 ATHENTI 어쩌구로 바꿀거임
    public String getChatRoomList(Model model) {
        Member 내계정 = null;
        List<ChatRoomDto> chatRoomDtoList
                = chatRoomService.getChatRoomList(내계정);
        if (chatRoomDtoList == null) model.addAttribute("isExist", false);
        // 채팅방 list 없으면 자바스크립트로 아예 접근 막거나 채팅이없다 이런 메시지를 채팅방 대신 띄우기
        model.addAttribute("chatRoomList", chatRoomDtoList);
        return "/chatRoom";
    }

    // 내계정 >> 어센틱 어쩌구로 변경
    @PostMapping("/chatRooom/writeChat/{roomId}")
    public Map<String, Object> write(@RequestBody ChatDto chatDto, @PathVariable int roomId) {
        Member 내계정 = null;
        ChatDto resultDto = chatRoomService.writeChat(chatDto, 내계정, roomId);

        return resultDto != null
                ? Map.of("chat", resultDto, "success", true)
                : Map.of("success", false);
    }

    // 결제 후 채팅으로 돌아가기 버튼 클릭 시 or 후기작성 완료 시
    @GetMapping("/chatRoom/paymentResult/{productId}")
    public String writePaymentResult(@PathVariable int productId, Member 내계정) {
        return chatRoomService.writePaymentResult(productId, 내계정) != null
                ? "redirect:/chatRoom/" + productId : "/";
    }
}
