package org.example.store.chatRoom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.chat.ChatDto;
import org.example.store.member.dto.CustomUserDetails;
import org.example.store.product.dto.ProductDto;
import org.example.store.product.entity.Product;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 최초 채팅
    // 구매자가 판매자에게 채팅 걸 때, 방이 있으면 채팅내역을 가져온다 <> 없으면 방을 만들어준다
    @GetMapping("/chatRoom/{productId}") // productId 는 해당상품, 판매자 조회 시 필요
    public String getChatRoomList(@PathVariable int productId, Model model,
                                  @AuthenticationPrincipal CustomUserDetails user) {
        Map<String, Object> resultMap = chatRoomService.getChatRoomListAndExist(user, productId);

        List<ChatRoomDto> chatRoomDtos = (List<ChatRoomDto>) resultMap.get("chatRoomList");
        List<ChatDto> chatDtos = (List<ChatDto>) resultMap.get("chatList");
        model.addAttribute("sellerRandomId", resultMap.get("sellerRandomId"));
        model.addAttribute("sellerName", resultMap.get("sellerName"));
        model.addAttribute("roomId", resultMap.get("roomId"));
        model.addAttribute("product", resultMap.get("product"));
        chatRoomDtos.forEach(chatRoomDto -> log.info("chatRoomDto === {}", chatRoomDto.toString()));
        model.addAttribute("chatRoomList", chatRoomDtos);
        model.addAttribute("chatList", chatDtos.isEmpty() ? new ArrayList<>() : chatDtos);
        return "chatting/chatRoom";
    }

    // 기존 채팅방 조회 >> 없으면 문구하나 정도 띄우기, 아니면 모달로 알려주기?
    @GetMapping("/chatRoom")
    public String getChatRoomList(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        List<ChatRoomDto> chatRoomDtoList = chatRoomService.getChatRoomList(user);
        // 채팅방 list 없으면 자바스크립트로 아예 접근 막거나 채팅이없다 이런 메시지를 채팅방 대신 띄우기
        chatRoomDtoList.forEach(chatRoomDto -> log.info("chatRoomDto === {}", chatRoomDto.toString()));
        model.addAttribute("chatRoomList", chatRoomDtoList);
        return "chatting/chatRoom";
    }

    @DeleteMapping("/chatRoom/delete/{roomId}")
    @ResponseBody
    public Map<String, Boolean> delete(@PathVariable int roomId) {
        boolean isDelete = chatRoomService.deleteChatRoom(roomId);
        return Map.of("isDelete", isDelete);
    }

    @PostMapping("/chatRoom/product/{roomId}")
    @ResponseBody
    public Map<String, Object> getProductByRoomId(@PathVariable int roomId) {
        ProductDto productDto = chatRoomService.getProductByRoomId(roomId);
        return Map.of("product", productDto, "isProduct", productDto.getProductId() > 0);
    }


    // 채팅 쓰기
    @PostMapping("/chatRoom/writeChat/{roomId}")
    @ResponseBody
    public Map<String, Object> write(@RequestBody ChatDto chatDto, @PathVariable int roomId,
                                     @AuthenticationPrincipal CustomUserDetails user) {
        ChatDto resultDto = chatRoomService.writeChat(chatDto, user, roomId);

        return resultDto != null
                ? Map.of("chat", resultDto, "success", true)
                : Map.of("success", false);
    }

    // 결제성공 후 채팅으로 돌아가기 버튼 클릭 시 or 후기작성 완료 시 채팅자동작성
    @GetMapping("/chatRoom/paymentResult/{productId}")
    public String writePaymentResult(@PathVariable int productId,
                                     @AuthenticationPrincipal CustomUserDetails user) {
        return chatRoomService.writePaymentResult(productId, user) != null
                ? "redirect:/chatting/chatRoom/" + productId : "/";
    }
}
