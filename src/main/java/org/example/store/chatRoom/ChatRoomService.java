package org.example.store_project.chatRoom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store_project.chat.ChatDto;
import org.example.store_project.chat.ChatService;
import org.example.store_project.member.Member;
import org.example.store_project.member.MemberService;
import org.example.store_project.product.ProductService;
import org.example.store_project.product.entity.Product;
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

    private final MemberService memberService;

    //안읽은 메시지 세주는 함수
    private void getUnreadCount(List<ChatRoomDto> chatRoomDtoList, String fromUserId) {
        int count = 0;
        for (ChatRoomDto existedRoomDto : chatRoomDtoList) {
            for (ChatDto unreadCountDto : existedRoomDto.getChatDtoList()) {
                String writerOfChat = unreadCountDto.getWriter().getUserId();
                if (writerOfChat.equals(fromUserId)) continue;
                if (!unreadCountDto.isRead()) count++;
            }
            existedRoomDto.setUnreadCount(count);
        }
    }

    // 판매자에게 채팅시도 때
    public List<ChatRoomDto> getChatRoomListAndExist(String fromUserId, int productId) {
        List<ChatRoomDto> resultRoomList = new ArrayList<>(); // 값을 돌려줄 채팅방 dto List

        List<ChatRoomDto> existedRoomDto = getChatRoomList(fromUserId); //사용자 기존 채팅방 조회
        if (existedRoomDto != null) { //기존 채팅방이 없을 수도 있음
            //각 채팅방 마다 사용자가 안읽은 수 세기 >> 위에 함수 참고
            getUnreadCount(existedRoomDto, fromUserId);

            //결과값에 기존 채팅방 List 추가
            resultRoomList.addAll(existedRoomDto);
        }

        //productId 로 상품 & toUser 조회
        Product product = productService.getProduct(productId);
        Member toUser = product.getMember();

        ChatRoomDto resultRoomDto; // 결과 List 에 추가 될 채팅방 dto
        ChatRoom existedChatRoom //둘이 거래한 채팅방 조회
                = chatRoomRepository.findChatRoomByUsers(fromUserId, toUser.getUserId());
        if (existedChatRoom != null) { //방이 있다면 채팅내역 추가
            List<ChatDto> chatDtoList = chatService.getList(existedChatRoom.getRoomId());
            resultRoomDto = ChatRoom.fromEntity(existedChatRoom);
            resultRoomDto.setChatDtoList(chatDtoList);
            resultRoomDto.setProductDto(Product.fromEntity(product)); // 거래 상품 교체
        } else { //방이 없다면 만든다
            //fromUserId 로 fromUser 조회
            Member fromUser = memberService.getMember(fromUserId);
            ChatRoom insertChatRoom = ChatRoom.builder() //테이블에 저장할 값 빌드
                    .fromUser(fromUser).toUser(toUser).product(product)
                    .build();
            // 채팅방 생성 후 dto 로 변환
            resultRoomDto = ChatRoom.fromEntity(chatRoomRepository.save(insertChatRoom));
        }
        resultRoomList.add(resultRoomDto); //결과값에 판매자/구매자 채팅방 추가

        //각 채팅방 마다 사용자가 안읽은 수 세기 >> 상단 함수참고
        getUnreadCount(resultRoomList, fromUserId);

        return resultRoomList;
    }

    // 메인에서 채팅페이지로 넘어갈 때
    public List<ChatRoomDto> getChatRoomList(String fromUserId) {
        // 내가 구매자이거나 판매자인 채팅방 찾기
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByUserId(fromUserId);
        if (chatRoomList.isEmpty()) return null; //채팅내역이 아예 없으면 null 던져준다
        else {
            //EntityList >> DtoList
            List<ChatRoomDto> chatRoomDtoList = ChatRoom.fromEntityList(chatRoomList);

            //각 채팅방 마다 사용자가 안읽은 수 세기 >> 상단 함수참고
            getUnreadCount(chatRoomDtoList, fromUserId);
            return chatRoomDtoList;
        }
    }

}
