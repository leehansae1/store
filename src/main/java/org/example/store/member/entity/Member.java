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
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

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

  @CreatedDate
  private LocalDateTime regDate;

  private String introduce;

  @Enumerated(EnumType.STRING)
  private MemberStatus status;

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
  public Member(String userId, String userPw, String userName, String userProfile, String userEmail,
                String address, String tel, Role role, LocalDateTime regDate, String introduce,
                MemberStatus status, List<Faq> faqList, List<Chat> chatList, List<ChatRoom> chatRoomList,
                List<Product> productList, List<Follow> followList, List<LikeProduct> likeProductList,
                List<Review> reviewList, List<Payment> paymentList) {
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
    this.status = status != null ? status : MemberStatus.STATUS_ACTIVE;
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
    this.userName  = modifyDto.getUserName();
    this.userEmail = modifyDto.getUserEmail();
    this.address   = modifyDto.getAddress();
    this.tel       = modifyDto.getTel();
    this.introduce = modifyDto.getIntroduce();
  }

  public void deleteMember() {
    this.status = MemberStatus.STATUS_DELETED;
  }

  public void setUserPw(String encodedNewPassword) {
    this.userPw = encodedNewPassword;
  }

  public void setUserProfile(String newProfilePath) {
    this.userProfile = newProfilePath;
  }

  public static MemberDto fromEntity(Member member) {
    return MemberDto.builder()
            .userId(member.getUserId())
            .userName(member.getUserName())
            .userProfile(member.getUserProfile())
            .userEmail(member.getUserEmail())
            .address(member.getAddress())
            .tel(member.getTel())
            .introduce(member.getIntroduce())
            .role(member.getRole())
            .regDate(member.getRegDate())
            .status(member.getStatus())
            .build();
  }
}
