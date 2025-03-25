package org.example.store.chatRoom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.chat.ChatDto;
import org.example.store.chat.ChatService;
import org.example.store.member.dto.CustomUserDetails;
import org.example.store.member.entity.Member;
import org.example.store.product.ProductService;
import org.example.store.product.dto.ProductDto;
import org.example.store.product.entity.Product;
import org.example.store.util.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    private final ChatService chatService;

    private final ProductService productService;

    // 판매자에게 채팅시도 때
    public Map<String, Object> getChatRoomListAndExist(CustomUserDetails user, int productId) {
        Map<String, Object> resultMap = new HashMap<>();
        List<ChatRoomDto> resultRoomDtoList = getChatRoomList(user); //사용자 기존 채팅방 조회

        // 내 계정 & productId로 상품 & toUser 조회
        Member fromUser = user.getLoggedMember();
        String fromUserId = fromUser.getUserId();
        resultMap.put("fromUserName", fromUser.getUserName()); //사용자와 판매자 구별

        Product product = productService.getProduct(productId);
        ProductDto productDto = Product.fromEntity(product);
        productDto.getSeller().setUserPw("");
        resultMap.put("product", productDto);

        Member toUser = product.getSeller();
        resultMap.put("toUserName", toUser.getUserName()); //사용자와 판매자 구별
        resultMap.put("toUserRandomId", toUser.getRandomId());


        ChatRoomDto existingChatRoom = null;

        if (!resultRoomDtoList.isEmpty()) { //기존 채팅방이 있다면
            log.info("기존채팅방이 있어요. 이제 두 계정의 채팅방을 찾아볼게요");
            //기존 채팅방에서 둘이 거래한 방 조회
            for (ChatRoomDto resultRoomDto : resultRoomDtoList) {
                String userId01 = resultRoomDto.getToUser().getUserId(); //기존 채팅방 유저01 아이디
                String userId02 = resultRoomDto.getFromUser().getUserId(); //기존 채팅방 유저02 아이디
                //안읽은 메시지 세기
                int unreadCount = chatService.getUnreadMessageCount(resultRoomDto.getRoomId(), fromUserId);
                resultRoomDto.setUnreadCount(unreadCount);

                // fromUser toUser 어디든 구매자, 판매자 아이디가 같이 들어있으면 됨
                if ((userId02.equals(fromUserId) && userId01.equals(toUser.getUserId())) ||
                        (userId01.equals(fromUserId) && userId02.equals(toUser.getUserId()))) {
                    log.info("기존채팅방을 찾았어요 !");
                    //결과값에 채팅내역 저장
                    resultMap.put("roomId", resultRoomDto.getRoomId());
                    existingChatRoom = resultRoomDto;
                }
            }
        }
        if (existingChatRoom == null) { //두 계정 사이의 채팅방이 없다면 만든다
            ChatRoomDto chatRoomDto = makeChatRoomDto(fromUser, toUser, product);
            resultMap.put("roomId", chatRoomDto.getRoomId());
            resultRoomDtoList.add(chatRoomDto);
            resultMap.put("chatList", new ArrayList<>());
        } else { //있다면 채팅내역 조회한다
            resultMap.put("chatList", chatService.getList(fromUser, existingChatRoom.getRoomId()));
            if (existingChatRoom.getProductDto().getProductId() != productId) {
                existingChatRoom.setProductDto(Product.fromEntity(product));
                chatRoomRepository.save(ChatRoomDto.toEntity(existingChatRoom));
            }
        }
        resultRoomDtoList.forEach(resultRoomDto -> {
            //직관적인 날짜
            String formatTime = DateUtils.getTimeAgo(resultRoomDto.getLatestMessageTime());
            resultRoomDto.setLatestTimeStr(formatTime);
            // 비밀번호 모두 지우기
            resultRoomDto.getProductDto().getSeller().setUserPw("");
            resultRoomDto.getToUser().setUserPw("");
            resultRoomDto.getFromUser().setUserPw("");
        });
        resultMap.put("chatRoomList", resultRoomDtoList); //결과값에 기존 or 새로만든 chatRoom 추가
        return resultMap;
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
        List<ChatRoomDto> chatRoomDtoList = chatRoomRepository.findAllChatRoomsWithLatestMessage(member.getUserId())
                .stream().map(ChatRoomDto::fromProjection).collect(Collectors.toList());
        if (!chatRoomDtoList.isEmpty()) { //비어있지 않다면 >> 기존 채팅방들이 있다면
            chatRoomDtoList.forEach(chatRoomDto -> {
                String formatDate = DateUtils.getTimeAgo(chatRoomDto.getLatestMessageTime());
                chatRoomDto.setLatestTimeStr(formatDate); //직관적인 날짜
                //안읽은 메시지 세기
                chatRoomDto.setUnreadCount(
                        chatService.getUnreadMessageCount(chatRoomDto.getRoomId(), member.getUserId())
                );
                // 비밀번호 모두 지우기
                chatRoomDto.getFromUser().setUserPw("");
                chatRoomDto.getToUser().setUserPw("");
                chatRoomDto.getProductDto().getSeller().setUserPw("");
                log.info("getChatRoomList's chatRoomDto's unreadCount === {}", chatRoomDto.getUnreadCount());
            });
            return chatRoomDtoList;
        }
        log.info("chatRoomList is empty {}", chatRoomDtoList);
        return new ArrayList<>(); //채팅방이 없으면 빈 리스트 하나 던져준다
    }

    @Transactional
    public boolean deleteChatRoom(int roomId) {
        chatRoomRepository.deleteById(roomId);
        return !chatRoomRepository.existsById(roomId);
    }

    // 채팅 쓰기
    public ChatDto writeChat(ChatDto chatDto, CustomUserDetails user, int roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);
        if (chatRoom != null) {
            chatDto.setChatRoomDto(ChatRoom.fromEntity(chatRoom));
            chatDto.setWriter(Member.fromEntity(user.getLoggedMember()));
            log.info("save target == {}", chatDto);
            log.info("new chat's room == {}", chatDto.getChatRoomDto());
            return chatService.write(chatDto);
        }
        return null;
    }

    // 결제 성공 창에서 채팅으로 돌아가기 버튼 클릭 시
    public ChatDto writePaymentResult(int productId, CustomUserDetails user) {
        Member member = user.getLoggedMember();
        ChatRoom chatRoom = chatRoomRepository
                .findByProductIdAndMember(productId, member.getUserId());
        Product product = productService.getProduct(productId);
        ChatDto chatDto;

        if (chatRoom != null) {
            log.info("payment chatRoom");
            chatDto = ChatDto.builder()
                    .chatRoomDto(ChatRoom.fromEntity(chatRoom))
                    .writer(Member.fromEntity(member))
                    .content(
                            "결제가 완료 되었습니다!\r\n" +
                                    "상품명: " + product.getProductName() + "\r\n" +
                                    "결제 금액: " + product.getPrice() + "원"
                    )
                    .productImgUrl(product.getThumbnailUrl())
                    .build();
        } else {
            ChatRoomDto newChatRoom
                    = makeChatRoomDto(member, product.getSeller(), product);
            chatDto = ChatDto.builder()
                    .chatRoomDto(newChatRoom)
                    .writer(Member.fromEntity(member))
                    .content(
                            "결제가 완료 되었습니다!\r\n" +
                                    "상품명: " + product.getProductName() + "\r\n" +
                                    "결제 금액: " + product.getPrice() + "원"
                    )
                    .productImgUrl(product.getThumbnailUrl())
                    .build();
        }
        return chatService.write(chatDto);
    }


    public ProductDto getProductByRoomId(int roomId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);

        ProductDto productDto = Product.fromEntity(chatRoom.getProduct());
        productDto.getSeller().setUserPw("");
        return productDto;
    }
}