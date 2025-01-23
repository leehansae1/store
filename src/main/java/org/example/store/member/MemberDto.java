package org.example.store.member;

import lombok.*;
import org.example.store_project.chat.Chat;
import org.example.store_project.chat.ChatDto;
import org.example.store_project.chatRoom.ChatRoom;
import org.example.store_project.chatRoom.ChatRoomDto;
import org.example.store_project.faq.Faq;
import org.example.store_project.faq.FaqDto;
import org.example.store_project.product.dto.ProductDto;
import org.example.store_project.product.entity.Product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MemberDto {

    private String userId;

    private String password;

    private String userName;

    private String userProfile;

    private String userEmail;

    private String address;

    private String tel;

    private LocalDateTime regDate;

    // 소개글
    private String introduce;

    private List<FaqDto> faqDtoList;

    private List<ChatDto> chatDtoList;

    private List<ChatRoomDto> chatRoomDtoList;

    // 우선 배제
    private List<ProductDto> productDtoList;

    @Builder
    public MemberDto(String userId, String password, String userName, String userProfile,
                     String userEmail, String address, String tel, LocalDateTime regDate,
                     String introduce, List<FaqDto> faqDtoList, List<ChatDto> chatDtoList,
                     List<ChatRoomDto> chatRoomDtoList,

                     List<Product> productList) {
        this.userId = userId;
        this.password = password;
        this.userName = userName;
        this.userProfile = userProfile;
        this.userEmail = userEmail;
        this.address = address;
        this.tel = tel;
        this.regDate = regDate;
        this.introduce = introduce;

        this.faqDtoList = faqDtoList;
        this.chatDtoList = chatDtoList;
        this.chatRoomDtoList = chatRoomDtoList;
    }

    public static Member toEntity(MemberDto memberDto) {
        //지금은 리스트들 다 주석처리 해주시면 됩니다 !
        List<Faq> faqs = new ArrayList<>();
        memberDto.getFaqDtoList().forEach(faqDto ->
                faqs.add(FaqDto.toEntity(faqDto))
        );

        List<Chat> chats = new ArrayList<>();
        memberDto.getChatDtoList().forEach(chatDto ->
                chats.add(ChatDto.toEntity(chatDto))
        );

        List<ChatRoom> chatRooms = new ArrayList<>();
        memberDto.getChatRoomDtoList().forEach(chatRoomDto ->
                chatRooms.add(ChatRoomDto.toEntity(chatRoomDto))
        );

        return Member.builder()
                .userId(memberDto.getUserId())
                .password(memberDto.getPassword())
                .userName(memberDto.getUserName())
                .userProfile(memberDto.getUserProfile())
                .userEmail(memberDto.getUserEmail())
                .address(memberDto.getAddress())
                .tel(memberDto.getTel())
                .introduce(memberDto.getIntroduce())
                .regDate(memberDto.getRegDate())

                .faqList(faqs)
                .chatList(chats)
                .chatRoomList(chatRooms)
                .build();
    }
}
