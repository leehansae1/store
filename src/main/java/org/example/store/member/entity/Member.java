package org.example.store.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.store.chat.Chat;
import org.example.store.chatRoom.ChatRoom;
import org.example.store.faq.Faq;
import org.example.store.follow.Follow;
import org.example.store.like_product.LikeProduct;
import org.example.store.member.constant.MemberStatus;
import org.example.store.member.constant.Role;
import org.example.store.member.dto.MemberDto;
import org.example.store.member.dto.ModifyDto;
import org.example.store.memberReview.Review;
import org.example.store.payment.Payment;
import org.example.store.product.entity.Product;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

  // @Id
  // @GeneratedValue(strategy = GenerationType.SEQUENCE)
  // private Integer id;

  @Column(unique = true)
  @Id
  private String userId; 

  private String userPw;

  private String userName;

  private String userProfile;

  private String userEmail;

  private String address;

  private String tel;

  @Enumerated(EnumType.STRING)
  private Role role;

  private LocalDateTime regDate;

  private String introduce;

  @Enumerated(EnumType.STRING)
  private MemberStatus status; // 회원 상태 (ACTIVE, DELETED)

  // 맵핑
  @OneToMany(mappedBy = "member")
  private List<Faq> faqList;

  @OneToMany(mappedBy = "writer")
  @OrderBy("chatDate desc")
  private List<Chat> chatList;

  @OneToMany(mappedBy = "toUser")
  private List<ChatRoom> chatRoomList;

  @OneToMany(mappedBy = "seller")
  private List<Product> productList;

  @OneToMany(mappedBy = "follower")
  private List<Follow> followList;

  @OneToMany(mappedBy = "liker")
  private List<LikeProduct> likeProductList;

  @OneToMany(mappedBy = "reviewer")
  private List<Review> reviewList;

  @OneToMany(mappedBy = "customer")
  private List<Payment> paymentList;

  @Builder
  public Member(String userId, String userPw, String userName, String userProfile, String userEmail, String address,
                String tel, Role role, LocalDateTime regDate, String introduce,
                MemberStatus status
          
                , List<Faq> faqList, List<Chat> chatList, List<ChatRoom> chatRoomList, List<Product> productList,
                List<Follow> followList, List<LikeProduct> likeProductList, List<Review> reviewList,
                List<Payment> paymentList
                ) {
    this.userId = userId;
    this.userPw = userPw;
    this.userName = userName;
    this.userProfile = userProfile;
    this.userEmail = userEmail;
    this.address = address;
    this.tel = tel;
    this.role = role;
    this.regDate = regDate;
    this.introduce = introduce;
    this.status = status != null ? status : MemberStatus.STATUS_ACTIVE; // 기본 값 설정

    this.faqList = faqList;
    this.chatList = chatList;
    this.chatRoomList = chatRoomList;
    this.productList = productList;
    this.followList = followList;
    this.likeProductList = likeProductList;
    this.reviewList = reviewList;
    this.paymentList = paymentList;
    }


  public void updateInfo(ModifyDto modifyDto) {
    // 수정된 정보를 member 객체에 반영
    this.userName  = modifyDto.getUserName();
    this.userEmail = modifyDto.getUserEmail();
    this.address    = modifyDto.getAddress();
    this.tel       = modifyDto.getTel();
    this.introduce = modifyDto.getIntroduce();
  }

  public void deleteMember() {
    this.status = MemberStatus.STATUS_DELETED; // 상태를 DELETED로 변경
  }


  public void setUserPw(String encodedNewPassword) {
    this.userPw = encodedNewPassword; // 새로운 패스워드 암호화 
  }


  public void setUserProfile(String newProfilePath) {
    this.userProfile = newProfilePath;
  }

public static MemberDto fromEntity(Member member) {
    // 멤버 테이블에 
        return MemberDto.builder()
                .userId(member.getUserId())
                // 데이터 조회 시 패스워드 제외
                .userName(member.getUserName())
                .userProfile(member.getUserProfile())
                .userEmail(member.getUserEmail())
                .address(member.getAddress())
                .tel(member.getTel())
                .introduce(member.getIntroduce())
                .role(member.getRole())
                .regDate(member.getRegDate())
                .status(member.getStatus())

                .productDtoList(Product.fromEntityList(member.getProductList()))
                .faqDtoList(Faq.fromEntityList(member.getFaqList()))
                .chatDtoList(Chat.fromEntityList(member.getChatList()))
                .chatRoomDtoList(ChatRoom.fromEntityList(member.getChatRoomList()))
                .followDtoList(Follow.fromEntityList(member.getFollowList()))
                .likeProductDtoList(LikeProduct.fromEntityList(member.getLikeProductList()))
                .paymentDtoList(Payment.fromEntityList(member.getPaymentList()))
                .reviewDtoList(Review.fromEntityList(member.getReviewList()))
                .build();
    }


}
