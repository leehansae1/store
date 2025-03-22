package org.example.store.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.constant.MemberStatus;
import org.example.store.member.dto.CustomUserDetails;
import org.example.store.member.entity.Member;
import org.example.store.member.repository.MemberRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Optional<Member> optionalMember = memberRepository.findByUserId(userId);

        if (optionalMember.isEmpty()) {
            log.warn("🚨 [로그인 실패] 존재하지 않는 사용자 userId: {}", userId);
            throw new BadCredentialsException("존재하지 않는 아이디입니다.");
        }

        Member member = optionalMember.get();

        if (member.getStatus() == MemberStatus.STATUS_DELETED) {
            log.warn("🚨 [로그인 실패] 탈퇴한 계정 시도 userId: {}", userId);
            throw new DisabledException("이 계정은 탈퇴 처리되었습니다. 새로운 계정을 생성하세요.");
        }

        return new CustomUserDetails(member);
    }
}

