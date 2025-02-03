package org.example.store.member.service;

import java.util.Optional;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.example.store.member.constant.MemberStatus;
import org.example.store.member.dto.CustomUserDetails;
import org.example.store.member.entity.Member;
import org.example.store.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService{

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
      // DB에서 userId에 해당하는 사용자를 조회
      Optional<Member> optionalMember = memberRepository.findByUserId(userId);

      if(optionalMember.isPresent()) {
        Member member = optionalMember.get();

        // DELETED 상태라면 로그인할 수 없게 처리
        if (member.getStatus() == MemberStatus.STATUS_DELETED) {
          throw new DisabledException("더 이상 사용할 수 없는 아이디입니다.");
        }

        // 사용자가 존재하고 ACTIVE 상태라면 반환
        return new CustomUserDetails(member);        
      }
      // 사용자가 없으면 예외
      throw new UsernameNotFoundException("아이디 패스워드를 확인해주세요.");
    } 
  }