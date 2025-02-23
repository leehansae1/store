package org.example.store.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.store.member.constant.MemberStatus;
import org.example.store.member.constant.Role;
import org.example.store.member.entity.Member;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class MemberDto {

    private String userId;
    private String userPw;
    private String userName;
    private String userProfile;
    private String userEmail;
    private String address;
    private String tel;
    private Role role;
    private LocalDateTime regDate;
    private String introduce;
    private MemberStatus status;
    private int followCount;
    private boolean followState;

    @Builder
    public MemberDto(String userId, String userPw, String userName, String userProfile,
                     String userEmail, String address, Role role, String tel, LocalDateTime regDate,
                     String introduce, MemberStatus status, int followCount, boolean followState) {
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
        this.followCount = followCount;
        this.followState = followState;
    }

    public static Member toEntity(MemberDto memberDto) {
        return Member.builder()
                .userId(memberDto.getUserId())
                .userPw(memberDto.getUserPw())
                .userName(memberDto.getUserName())
                .userProfile(memberDto.getUserProfile())
                .userEmail(memberDto.getUserEmail())
                .address(memberDto.getAddress())
                .tel(memberDto.getTel())
                .introduce(memberDto.getIntroduce())
                .role(memberDto.getRole())
                .regDate(memberDto.getRegDate())
                .status(memberDto.getStatus() != null ? memberDto.getStatus() : MemberStatus.STATUS_ACTIVE)
                .build();
    }
}
