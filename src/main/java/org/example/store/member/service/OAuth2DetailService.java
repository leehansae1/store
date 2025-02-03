package com.dragontiger.prjectwave.member.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.dragontiger.prjectwave.member.constant.Role;
import com.dragontiger.prjectwave.member.dto.CustomUserDetails;
import com.dragontiger.prjectwave.member.entity.Member;
import com.dragontiger.prjectwave.member.repository.MemberRepository;
import com.dragontiger.prjectwave.member.social.GoogleUserInfo;
import com.dragontiger.prjectwave.member.social.KakaoUserInfo;
import com.dragontiger.prjectwave.member.social.SocialUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2DetailService extends DefaultOAuth2UserService{

  private final MemberRepository memberRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    // 1. OAuth2 사용자 정보 로드 (소셜 로그인 제공자)
    OAuth2User oAuth2User = super.loadUser(userRequest); // OAuth2UserRequest를 사용해 실제 사용자 정보를 가져옴
    log.info("userRequset === {}",userRequest);

    // 2. 소셜 사용자 정보 추출
    Map<String,Object> oauth2UserInfo = (Map) oAuth2User.getAttributes(); // OAuth2User에서 소셜 로그인 제공자의 사용자 정보를 가져옴
    log.info("(Map)oauth2User.getAttributes() === {}", oAuth2User.getAttributes());

    // 3. 소셜 로그인 제공자 확인
    String provider = userRequest.getClientRegistration().getRegistrationId(); // 소셜 로그인 제공자 이름
    log.info("userRequest === {}", userRequest.getClientRegistration().getRegistrationId());
  
    SocialUserInfo socialUserInfo = null;

    // 4. 구글, 카카오 로그인의 경우 처리 (다른 소셜 제공자는 필요 시 추가)
    if (provider.equals("google")) {
      socialUserInfo = new GoogleUserInfo(oauth2UserInfo);
    } else if (provider.equals("kakao")) {
      socialUserInfo = new KakaoUserInfo(oauth2UserInfo);
    } 

    // 5. 이미 존재하는 회원인지 확인 (이미 등록된 회원이라면 그 정보를 반환)
    Member returnMember = null;
    Optional<Member> findMember = memberRepository.findByUserId(socialUserInfo.getProviderId()); // 소셜 로그인 사용자 ID로 회원 조회
    if (findMember.isPresent()) {
      returnMember = findMember.get();
    } else {
      // 새로운 사용자라면 회원가입 진행
      Member member = Member.builder()
                            .userId(socialUserInfo.getProviderId())
                            .userName(socialUserInfo.getName())
                            .userEmail(socialUserInfo.getEmail())
                            .userPw(bCryptPasswordEncoder.encode(UUID.randomUUID().toString())) // 패스워드는 랜덤으로 생성 후 암호화
                            .role(Role.ROLE_USER)
                            .regDate(LocalDateTime.now())                                            
                            .build();
      returnMember = memberRepository.save(member); // 새로운 사용자 정보 저장
    }

    // 6. 최종적으로 CustomUserDetails 객체를 만들어서 반환
    // CustomUserDetails는 UserDetails 인터페이스를 구현하여 Spring Security에서 사용할 수 있도록 함
    return new CustomUserDetails(returnMember,oAuth2User.getAttributes());
  }

}
