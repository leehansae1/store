package org.example.store.chatRoom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store_project.chat.Chat;
import org.example.store_project.chat.ChatDto;
import org.example.store_project.chat.ChatRepository;
import org.example.store_project.member.Member;
import org.example.store_project.member.MemberRepository;
import org.example.store_project.product.ProductRepository;
import org.example.store_project.product.entity.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    private final ChatRepository chatRepository;

    private final ProductRepository productRepository;

    private final MemberRepository memberRepository;

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

        Member toUser; //productId 로 toUser 조회
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) toUser = optionalProduct.get().getMember();
        else return null;

        ChatRoom existedChatRoom //둘이 거래한 채팅방을 찾는다
                = chatRoomRepository.findChatRoomBy(fromUserId, toUser.getUserId());
        ChatRoomDto resultRoomDto; // 결과 List 에 추가 될 채팅방 dto
        if (existedChatRoom != null) { //방이 있다면 채팅내역 가져온다
            List<Chat> chatList
                    = chatRepository.findAllByChatRoom_RoomId(existedChatRoom.getRoomId());
            resultRoomDto = ChatRoom.fromEntity(existedChatRoom);
            resultRoomDto.setChatDtoList(Chat.fromEntityList(chatList));
        } else { //방이 없다면 만든다
            Member fromUser; //fromUserId 로 fromUser 조회
            Optional<Member> optionalMember = memberRepository.findByUserId(fromUserId);
            if (optionalMember.isPresent()) fromUser = optionalMember.get();
            else return null;
            ChatRoom insertChatRoom = ChatRoom.builder() //테이블에 저장할 값 빌드
                    .fromUser(fromUser).toUser(toUser)
                    .build();
            // 채팅방 생성 후 dto 로 변환
            resultRoomDto = ChatRoom.fromEntity(chatRoomRepository.save(insertChatRoom));
        }

        List<ChatRoomDto> existedRoom = getChatRoomList(fromUserId); //사용자 기존 채팅방 조회
        if (existedRoom != null) { //기존 채팅방이 없을 수도 있음
            //각 채팅방 마다 사용자가 안읽은 수 세기 >> 위에 함수 참고
            getUnreadCount(existedRoom, fromUserId);

            //결과값에 기존 채팅방 List 추가
            resultRoomList.addAll(existedRoom);
        }

        resultRoomList.add(resultRoomDto); //결과값에 판매자/구매자 채팅방 추가
        return resultRoomList;
    }

    // 판매자에게 채팅시도 때
    public List<ChatRoomDto> getChatRoomList(String fromUserId) {
        // 내가 구매자이거나 판매자인 채팅방 찾기
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByUserId(fromUserId);
        if (chatRoomList.isEmpty()) return null; //채팅내역이 아예 없으면 null 던져준다
        else {
            //EntityList >> DtoList
            List<ChatRoomDto> chatRoomDtoList = ChatRoom.fromEntityList(chatRoomList);
            //각 채팅방 마다 사용자가 안읽은 수 세기 >> 위에 함수참고
            getUnreadCount(chatRoomDtoList, fromUserId);
            return chatRoomDtoList;
        }
    }

}
