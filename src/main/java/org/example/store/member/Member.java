package org.example.store.member;

import jakarta.persistence.*;
import lombok.*;
import org.example.store_project.chat.Chat;
import org.example.store_project.chat.ChatDto;
import org.example.store_project.chatRoom.ChatRoom;
import org.example.store_project.chatRoom.ChatRoomDto;
import org.example.store_project.faq.Faq;
import org.example.store_project.faq.FaqDto;
import org.example.store_project.product.entity.Product;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Getter
public class Member {

    @Id
    private String userId;

    private String password;

    private String userName;

    private String userProfile;

    private String userEmail;

    private String address;

    private String tel;

    @CreatedDate
    private LocalDateTime regDate;

    // 소개글
    private String introduce;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Faq> faqList;

    @OneToMany(fetch = FetchType.EAGER)
    @OrderBy("chatDate desc")
    private List<Chat> chatList;

    @OneToMany(fetch = FetchType.EAGER)
    private List<ChatRoom> chatRoomList;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Product> productList;


    @Builder
    public Member(String userId, String password, String userName, String userProfile,
                  String userEmail, String address, String tel, LocalDateTime regDate,
                  String introduce, List<Faq> faqList, List<Chat> chatList,
                  List<ChatRoom> chatRoomList,

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

        this.faqList = faqList;
        this.chatList = chatList;
        this.chatRoomList = chatRoomList;
    }

    public static MemberDto fromEntity(Member member) {
        //지금은 리스트들 다 주석처리 해주시면 됩니다 !
        List<FaqDto> faqDtos = new ArrayList<>();
        member.getFaqList().forEach(faq ->
                faqDtos.add(Faq.fromEntity(faq))
        );

        List<ChatDto> chatDtos = new ArrayList<>();
        member.getChatList().forEach(chat ->
                chatDtos.add(Chat.fromEntity(chat))
        );

        List<ChatRoomDto> chatRoomDtos = new ArrayList<>();
        member.getChatRoomList().forEach(chatRoom ->
                chatRoomDtos.add(ChatRoom.fromEntity(chatRoom))
        );
        // 밑에 memberDto.builer().build() 하시면 됩니다 !
        return MemberDto.builder()
                .userId(member.getUserId())
                .password(member.getPassword())
                .userName(member.getUserName())
                .userProfile(member.getUserProfile())
                .userEmail(member.getUserEmail())
                .address(member.getAddress())
                .tel(member.getTel())
                .introduce(member.getIntroduce())
                .regDate(member.getRegDate())

                .faqDtoList(faqDtos)
                .chatDtoList(chatDtos)
                .chatRoomDtoList(chatRoomDtos)
                .build();
    }
}
