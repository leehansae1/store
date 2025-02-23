package org.example.store.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.follow.FollowService;
import org.example.store.member.constant.MemberStatus;
import org.example.store.member.constant.Role;
import org.example.store.member.dto.CustomUserDetails;
import org.example.store.member.dto.MemberDto;
import org.example.store.member.dto.ModifyDto;
import org.example.store.member.dto.SignupDto;
import org.example.store.member.entity.Member;
import org.example.store.member.repository.MemberRepository;
import org.example.store.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService implements IMemberService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberRepository memberRepository;
    private final FollowService followService;

    @Value("${file.path}")
    private String folderPath; // 파일 저장 경로

    @Override
    public Member signup(SignupDto signupDto) {
        // 프로필 이미지 파일 업로드 처리
        String userProfilePath = null;
        if (signupDto.getUserProfile() != null && !signupDto.getUserProfile().isEmpty()) {
            userProfilePath = FileUtil.saveAndRenameFile(signupDto.getUserProfile(), folderPath, 0);
        }
        // 패스워드 암호화
        String encodedPassword = bCryptPasswordEncoder.encode(signupDto.getUserPw());
        // role: null이면 기본 ROLE_USER로 설정
        Role role = signupDto.getRole() != null ? Role.valueOf(signupDto.getRole()) : Role.ROLE_USER;
        MemberDto memberDto = MemberDto.builder()
                .userId(signupDto.getUserId())
                .userPw(encodedPassword)
                .userEmail(signupDto.getUserEmail())
                .userName(signupDto.getUserName())
                .userProfile(userProfilePath)
                .tel(signupDto.getTel())
                .address(signupDto.getAddress())
                .introduce(signupDto.getIntroduce())
                .role(role)
                .status(MemberStatus.STATUS_ACTIVE)
                .regDate(LocalDateTime.now())
                .build();
        Member savedMember = MemberDto.toEntity(memberDto);
        return memberRepository.save(savedMember);
    }

    @Transactional
    public Member modifiedMember(ModifyDto modifyDto) {
        log.info("Modifying member with ID: {}", modifyDto.getUserId());
        Optional<Member> optionalMember = memberRepository.findByUserId(modifyDto.getUserId());
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            if (modifyDto.getUserPw() != null && !modifyDto.getUserPw().isEmpty()) {
                String encodedPassword = bCryptPasswordEncoder.encode(modifyDto.getUserPw());
                member.setUserPw(encodedPassword);
            }
            if (modifyDto.getUserProfile() != null && !modifyDto.getUserProfile().isEmpty()) {
                String newProfileName = FileUtil.saveAndRenameFile(modifyDto.getUserProfile(), folderPath, 0);
                member.setUserProfile(newProfileName);
            }
            member.updateInfo(modifyDto);
            return memberRepository.save(member);
        }
        log.warn("Member not found for ID: {}", modifyDto.getUserId());
        return null;
    }

    @Transactional
    public boolean deleteMember(String userId, String userPw) {
        Optional<Member> optionalMember = memberRepository.findByUserId(userId);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            if (!bCryptPasswordEncoder.matches(userPw, member.getUserPw())) {
                return false;
            }
            member.deleteMember();
            memberRepository.save(member);
            return true;
        }
        return false;
    }

    public List<Member> getDeletedMembers() {
        return memberRepository.findByStatus(MemberStatus.STATUS_DELETED);
    }

    @Override
    public Member findByUserId(String userId) {
        return memberRepository.findByUserId(userId).orElse(null);
    }

    @Override
    public ModifyDto getMemberById(String loginUserId) {
        Optional<Member> optionalMember = memberRepository.findByUserId(loginUserId);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            return ModifyDto.builder()
                    .userId(member.getUserId())
                    .userName(member.getUserName())
                    .userEmail(member.getUserEmail())
                    .address(member.getAddress())
                    .tel(member.getTel())
                    .introduce(member.getIntroduce())
                    .build();
        }
        throw new RuntimeException("회원 정보를 찾을 수 없습니다: " + loginUserId);
    }

    public Member getMember(String userId) {
        return memberRepository.findByUserId(userId).orElse(null);
    }

    public boolean follow(String sellerId, CustomUserDetails user) {
        Member seller = getMember(sellerId);
        return followService.follow(seller, user.getLoggedMember());
    }

    public int unfollow(String sellerId, CustomUserDetails user) {
        Member seller = getMember(sellerId);
        return followService.unfollow(seller, user.getLoggedMember());
    }
}
