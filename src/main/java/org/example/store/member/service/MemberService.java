package org.example.store.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.follow.FollowService;
import org.example.store.member.constant.MemberStatus;
import org.example.store.member.constant.Role;
import org.example.store.member.dto.CustomUserDetails;
import org.example.store.member.dto.MemberDto;
import org.example.store.member.entity.Member;
import org.example.store.member.repository.MemberRepository;
import org.example.store.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberRepository memberRepository;
    private final FollowService followService;

    @Value("${file.path}")
    private String folderPath; // 파일 저장 경로

    public boolean signup(MemberDto memberDto) {
        // 프로필 이미지 파일 업로드 처리
        if (memberDto.getProfile() != null && !memberDto.getProfile().isEmpty()) {
            String userProfilePath = FileUtil.saveAndRenameFile(memberDto.getProfile(), folderPath, 0);
            memberDto.setUserProfile(userProfilePath);
        }
        // 패스워드 암호화
        String encodedPassword = bCryptPasswordEncoder.encode(memberDto.getUserPw());
        memberDto.setUserPw(encodedPassword);
        // enum 컬럼들 채우기
        memberDto.setRole(
                memberDto.getRoleStr().equals("ROLE_USER") ? Role.ROLE_USER : Role.ROLE_ADMIN
        );
        memberDto.setStatus(MemberStatus.STATUS_ACTIVE);

        Member member = memberRepository.save(MemberDto.toEntity(memberDto));
        return Member.fromEntity(member) != null;
    }

    @Transactional
    public Member modifiedMember(MemberDto memberDto) {
        log.info("Modifying member with ID: {}", memberDto.getUserId());
        MemberDto foundMember = getMemberDto(memberDto.getUserId());
        String encodedPassword = bCryptPasswordEncoder.encode(memberDto.getUserPw());
        foundMember.setUserPw(encodedPassword);

        String newProfileName = FileUtil.saveAndRenameFile(memberDto.getProfile(), folderPath, 0);
        foundMember.setUserProfile(newProfileName);
        return memberRepository.save(MemberDto.toEntity(foundMember));
    }

    @Transactional
    public boolean hideMember(String userId, String userPw) {
        MemberDto memberDto = getMemberDto(userId);
        if (!bCryptPasswordEncoder.matches(userPw, memberDto.getUserPw())) {
            throw new IllegalArgumentException("Wrong password");
        }
        memberDto.setStatus(MemberStatus.STATUS_DELETED);
        Member member = memberRepository.save(MemberDto.toEntity(memberDto));
        return Member.fromEntity(member) != null;
    }

    public List<Member> getDeletedMembers() {
        return memberRepository.findByStatus(MemberStatus.STATUS_DELETED);
    }

    public Member findByUserId(String userId) {
        return memberRepository.findByUserId(userId).orElse(null);
    }

    public Member getMember(String userId) {
        return memberRepository.findByUserId(userId).orElse(null);
    }

    public MemberDto getMemberDto(String userId) {
        Optional<Member> optionalMember = memberRepository.findByUserId(userId);
        if (optionalMember.isPresent()) return Member.fromEntity(optionalMember.get());
        else return MemberDto.builder().userId("조회불가").build();
    }

    public boolean follow(String sellerId, CustomUserDetails user) {
        Member seller = getMember(sellerId);
        return followService.follow(seller, user.getLoggedMember());
    }

    public int unfollow(String sellerId, CustomUserDetails user) {
        Member seller = getMember(sellerId);
        return followService.unfollow(seller, user.getLoggedMember());
    }

    public boolean isExistedId(String userId) {
        return memberRepository.existsByUserId(userId);
    }
}
