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
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    private final ChatService chatService;

    private final ProductService productService;

    //안읽은 메시지 세주는 함수
    private void getUnreadCount(List<ChatRoomDto> chatRoomDtoList, Member member) {
        int count = 0;
        for (ChatRoomDto existedRoomDto : chatRoomDtoList) {
            for (ChatDto unreadCountDto : existedRoomDto.getChatDtoList()) {
                MemberDto writer = unreadCountDto.getWriter();
                if (MemberDto.toEntity(writer).equals(member)) continue;
                if (!unreadCountDto.isRead()) count++;
            }
            existedRoomDto.setUnreadCount(count);
        }
    }

    // 판매자에게 채팅시도 때
    public List<ChatRoomDto> getChatRoomListAndExist(CustomUserDetails user, int productId) {
        Member member = user.getLoggedMember();
        List<ChatRoomDto> resultRoomList = new ArrayList<>(); // 값을 돌려줄 채팅방 dto List

        List<ChatRoomDto> existedRoomDto = getChatRoomList(user); //사용자 기존 채팅방 조회
        if (existedRoomDto != null) { //기존 채팅방이 없을 수도 있음
            //각 채팅방 마다 사용자가 안읽은 수 세기 >> 위에 함수 참고
            getUnreadCount(existedRoomDto, member);

            //결과값에 기존 채팅방 List 추가
            resultRoomList.addAll(existedRoomDto);
        }

        //productId 로 상품 & toUser 조회
        Product product = productService.getProduct(productId);
        Member toUser = product.getSeller();
        String fromUserId = member.getUserId();

        ChatRoomDto resultRoomDto; // 결과 List 에 추가 될 채팅방 dto
        ChatRoom existedChatRoom //둘이 거래한 채팅방 조회
                = chatRoomRepository.findChatRoomByUsers(fromUserId, toUser.getUserId());
        if (existedChatRoom != null) { //방이 있다면 채팅내역 추가
            List<ChatDto> chatDtoList = chatService.getList(existedChatRoom.getRoomId());
            resultRoomDto = ChatRoom.fromEntity(existedChatRoom);
            resultRoomDto.setChatDtoList(chatDtoList);
            resultRoomDto.setProductDto(Product.fromEntity(product)); // 거래 상품 교체
        } else { //방이 없다면 만든다
            ChatRoom insertChatRoom = ChatRoom.builder() //테이블에 저장할 값 빌드
                    .fromUser(member).toUser(toUser).product(product)
                    .build();
            resultRoomDto = ChatRoom.fromEntity(
                    chatRoomRepository.save(insertChatRoom)
            );
        }
        resultRoomList.add(resultRoomDto); //결과값에 판매자와 구매자의 채팅방 추가

        //각 채팅방 마다 사용자가 안읽은 수 세기 >> 상단 함수참고
        getUnreadCount(resultRoomList, member);

        return resultRoomList;
    }

    // 메인에서 채팅페이지로 넘어갈 때
    public List<ChatRoomDto> getChatRoomList(CustomUserDetails user) {
        Member member = user.getLoggedMember();
        // 내가 구매자이거나 판매자인 채팅방 찾기
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByUserId(member.getUserId());
        if (!chatRoomList.isEmpty()) {
            List<ChatRoomDto> chatRoomDtoList = ChatRoom.fromEntityList(chatRoomList);

            //각 채팅방 마다 사용자가 안읽은 수 세기 >> 상단 함수참고
            getUnreadCount(chatRoomDtoList, member);
            return chatRoomDtoList;
        }
        return null; //채팅방이 없으면 null 던져준다
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

    // 결제 후 채팅으로 돌아가기 버튼 클릭 시
    public ChatDto writePaymentResult(int productId, CustomUserDetails user) {
        Member member = user.getLoggedMember();
        ChatRoom chatRoom = chatRoomRepository
                .findByProduct_ProductIdAndToUserOrFromUser(productId, member, member);
        if (chatRoom != null) {
            log.info("payment chatRoom");
            Product product = productService.getProduct(productId);
            ChatDto chatDto = ChatDto.builder()
                    .chatRoomDto(ChatRoom.fromEntity(chatRoom))
                    .writer(Member.fromEntity(member))
                    .content(
                            "결제가 완료 되었습니다!\r\n" +
                                    product.getProductName() + "\r\n" +
                                    "결제 금액: " + product.getPrice() + "원"
                    )
                    .build();
            return chatService.write(chatDto);
        }
        return null;
    }
}