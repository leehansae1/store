package org.example.store.member.dto;

import lombok.*;
import org.example.store.member.constant.MemberStatus;
import org.example.store.member.constant.Role;
import org.example.store.member.entity.Member;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MemberDto {

    private String userId;
    private String userPw;
    private int randomId;
    private String userName;
    private MultipartFile profile;
    private String userEmail;
    private String address;
    private String tel;
    private Role role;
    private LocalDateTime regDate;
    private String introduce;
    private MemberStatus status;
    private int followCount;
    private boolean followState;
    private int productCount;

    private String userProfile;
    private String roleStr;

    @Builder
    public MemberDto(String userId, String userPw, String userName, String userProfile, MultipartFile profile,
                     String userEmail, String address, Role role, String tel, LocalDateTime regDate, int randomId,
                     String introduce, MemberStatus status, int followCount, boolean followState, String roleStr,
                     int productCount) {
        this.productCount = productCount;
        this.randomId = randomId;
        this.roleStr = roleStr;
        this.profile = profile;
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
                .randomId(memberDto.getRandomId())
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
                .status(memberDto.getStatus())
                .build();
    }
}
