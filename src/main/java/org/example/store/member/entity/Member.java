package org.example.store.member.entity;

import java.time.LocalDateTime;

import org.example.store.member.constant.MemberStatus;
import org.example.store.member.constant.Role;
import org.example.store.member.dto.MemberDto;
import org.example.store.member.dto.ModifyDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

  private String addr01;

  private String addr02;

  private String zipcode;

  private String tel;

  @Enumerated(EnumType.STRING)
  private Role role;

  private LocalDateTime regDate;

  private String introduce;

  @Enumerated(EnumType.STRING)
  private MemberStatus status; // 회원 상태 (ACTIVE, DELETED)


  @Builder
  public Member(String userId, String userPw, String userName, String userProfile, String userEmail, String addr01,
                String addr02, String zipcode, String tel, Role role, LocalDateTime regDate, String introduce, MemberStatus status) {
    this.userId = userId;
    this.userPw = userPw;
    this.userName = userName;
    this.userProfile = userProfile;
    this.userEmail = userEmail;
    this.addr01 = addr01;
    this.addr02 = addr02;
    this.zipcode = zipcode;
    this.tel = tel;
    this.role = role;
    this.regDate = regDate;
    this.introduce = introduce;
    this.status = status != null ? status : MemberStatus.STATUS_ACTIVE; // 기본 값 설정            
               }


  public void updateInfo(ModifyDto modifyDto) {
    // 수정된 정보를 member 객체에 반영
    this.userName  = modifyDto.getUserName();
    this.userEmail = modifyDto.getUserEmail();
    this.addr01    = modifyDto.getAddr01();
    this.addr02    = modifyDto.getAddr02();
    this.zipcode   = modifyDto.getZipcode();
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
                .addr01(member.getAddr01())
                .addr02(member.getAddr02())
                .zipcode(member.getZipcode())
                .tel(member.getTel())
                .introduce(member.getIntroduce())
                .role(Role.ROLE_USER)
                .regDate(member.getRegDate())
                .status(member.getStatus())

                .productDtoList(Product.fromEntityList(member.getProductList()))
                .faqDtoList(Faq.fromEntityList(member.getFaqList()))
                .chatDtoList(Chat.fromEntityList(member.getChatList()))
                .chatRoomDtoList(ChatRoom.fromEntityList(member.getChatRoomList()))
                .build();
    }




  

}
