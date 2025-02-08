package org.example.store.chatRoom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.chat.ChatDto;
import org.example.store.chat.ChatService;
import org.example.store.member.dto.CustomUserDetails;
import org.example.store.member.entity.Member;
import org.example.store.member.dto.MemberDto;
import org.example.store.product.ProductService;
import org.example.store.product.entity.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    private final ChatService chatService;

    private final ProductService productService;

    //안읽은 메시지 세주는 함수
    private void getUnreadCount(List<ChatRoomDto> chatRoomDtoList, Member member) {
        for (ChatRoomDto existedRoomDto : chatRoomDtoList) {
            int count = 0;
            for (ChatDto unreadCountDto : existedRoomDto.getChatDtoList()) {
                MemberDto writer = unreadCountDto.getWriter();
                if (writer.getUserId().equals(member.getUserId())) continue;
                if (!unreadCountDto.isRead()) count++;
            }
            existedRoomDto.setUnreadCount(count);
        }
    }

    // 판매자에게 채팅시도 때
    public Map<String, Object> getChatRoomListAndExist(CustomUserDetails user, int productId) {
        Map<String, Object> result = new HashMap<>();
        List<ChatRoomDto> existedRoomDtoList = getChatRoomList(user); //사용자 기존 채팅방 조회

        // 내 계정 & productId로 상품 & toUser 조회
        Member fromUser = user.getLoggedMember();
        Product product = productService.getProduct(productId);
        Member toUser = product.getSeller();
        String fromUserId = fromUser.getUserId();

        if (!existedRoomDtoList.isEmpty()) { //기존 채팅방이 있다면

            //기존 채팅방에서 둘이 거래한 방 조회
            int resultNum = 0;
            for (int i = 0; i < existedRoomDtoList.size(); i++) {
                ChatRoomDto existedRoomDto = existedRoomDtoList.get(i);
                String userId01 = existedRoomDto.getToUser().getUserId(); //기존 채팅방 유저01 아이디
                String userId02 = existedRoomDto.getFromUser().getUserId(); //기존 채팅방 유저02 아이디

                // fromUser toUser 어디든 구매자, 판매자 아이디가 같이 들어있으면 됨
                if ((userId02.equals(fromUserId) && userId01.equals(toUser.getUserId()))
                        || (userId01.equals(fromUserId) && userId02.equals(toUser.getUserId()))) {
                    List<ChatDto> chatDtoList //채팅 내역 조회
                            = chatService.getList(fromUser, existedRoomDtoList.get(i).getRoomId());
                    if (chatDtoList != null) result.put("chatList", chatDtoList); //결과값에 채팅내역 저장
                    existedRoomDtoList.get(i).setProductDto(Product.fromEntity(product)); //기존 리스트에서도 상품 변경
                    chatRoomRepository //변경한 상품으로 수정
                            .save(ChatRoomDto.toEntity(existedRoomDtoList.get(i)));
                    resultNum = 1;
                }
            }
            //없다면 만든다
            if (resultNum == 0) {
                ChatRoomDto chatRoomDto = makeChatRoomDto(fromUser, toUser, product);
                existedRoomDtoList.add(chatRoomDto);
            }
        } else { //없다면 만든다
            ChatRoomDto chatRoomDto = makeChatRoomDto(fromUser, toUser, product);
            existedRoomDtoList.add(chatRoomDto);
        }
        result.put("chatRoomList", existedRoomDtoList); //결과값에 기존 or 새로만든 chatRoom List 추가

        //각 채팅방 마다 사용자가 안읽은 수 세기 >> 상단 함수참고
        getUnreadCount((List<ChatRoomDto>) result.get("chatRoomList"), fromUser);
        return result;
    }

    // 새로운 채팅방 개설
    private ChatRoomDto makeChatRoomDto(Member fromUser, Member toUser, Product product) {
        ChatRoom insertChatRoom //테이블에 저장할 값 빌드
                = ChatRoom.builder().fromUser(fromUser).toUser(toUser).product(product)
                .build();
        return ChatRoom.fromEntity(chatRoomRepository.save(insertChatRoom));
    }

    // 메인에서 채팅페이지로 넘어갈 때
    public List<ChatRoomDto> getChatRoomList(CustomUserDetails user) {
        Member member = user.getLoggedMember();
        // 내가 구매자이거나 판매자인 채팅방 찾기
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByUserId(member.getUserId());
        if (!chatRoomList.isEmpty()) { //비어있지 않다면 >> 기존 채팅방들이 있다면
            List<ChatRoomDto> chatRoomDtoList = ChatRoom.fromEntityList(chatRoomList);
            chatRoomDtoList.forEach(chatRoomDto ->
                    chatRoomDto.setChatDtoList(chatService.justCountUnread(chatRoomDto.getRoomId()))
            );
            //각 채팅방 마다 사용자가 안읽은 수 세기 >> 상단 함수참고
            getUnreadCount(chatRoomDtoList, member);
            return chatRoomDtoList;
        }
        return new ArrayList<>(); //채팅방이 없으면 빈 리스트 하나 던져준다
    }

    // 채팅 쓰기
    public ChatDto writeChat(ChatDto chatDto, CustomUserDetails user, int roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);
        if (chatRoom != null) {
            log.info("exist chatRoom");
            chatDto.setChatRoomDto(ChatRoom.fromEntity(chatRoom));
            chatDto.setWriter(Member.fromEntity(user.getLoggedMember()));
            return chatService.write(chatDto);
        }
        return null;
    }

    // 결제 성공 창에서 채팅으로 돌아가기 버튼 클릭 시
    public ChatDto writePaymentResult(int productId, CustomUserDetails user) {
        Member member = user.getLoggedMember();
        ChatRoom chatRoom = chatRoomRepository
                .findByProductIdAndMember(productId, member.getUserId());
        if (chatRoom != null) {
            log.info("payment chatRoom");
            Product product = productService.getProduct(productId);
            ChatDto chatDto = ChatDto.builder()
                    .chatRoomDto(ChatRoom.fromEntity(chatRoom))
                    .writer(Member.fromEntity(member))
                    .content(
                            "결제가 완료 되었습니다!\r\n" +
                                    "상품명: " + product.getProductName() + "\r\n" +
                                    "결제 금액: " + product.getPrice() + "원"
                    )
                    .build();
            return chatService.write(chatDto);
        }
        return null;
    }
}