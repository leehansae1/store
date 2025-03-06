package org.example.store.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.constant.MemberStatus;
import org.example.store.member.constant.Role;
import org.example.store.member.dto.CustomUserDetails;
import org.example.store.member.entity.Member;
import org.example.store.member.repository.MemberRepository;
import org.example.store.member.social.GoogleUserInfo;
import org.example.store.member.social.KakaoUserInfo;
import org.example.store.member.social.SocialUserInfo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2DetailService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> oauth2UserInfo = oAuth2User.getAttributes();
        String provider = userRequest.getClientRegistration().getRegistrationId();
        log.info("Provider: {}", provider);
        log.info("oauth2UserInfo: {}", oauth2UserInfo.toString());

        SocialUserInfo socialUserInfo = null;
        if ("google".equals(provider)) {
            socialUserInfo = new GoogleUserInfo(oauth2UserInfo);
        }
        if ("kakao".equals(provider)) {
            socialUserInfo = new KakaoUserInfo(oauth2UserInfo);
        }

        Member returnMember;
        Optional<Member> findMember = memberRepository.findByUserId(socialUserInfo.getProviderId());
        if (findMember.isPresent()) returnMember = findMember.get();
        else {
            Member member = Member.builder()
                    .userId(socialUserInfo.getProviderId())
                    .userName(socialUserInfo.getName())
                    .userEmail(socialUserInfo.getEmail())
                    .userProfile(socialUserInfo.getImageUrl())
                    .userPw(bCryptPasswordEncoder.encode(UUID.randomUUID().toString()))
                    .role(Role.ROLE_USER)
                    .status(MemberStatus.STATUS_ACTIVE)
                    .build();
            returnMember = memberRepository.save(member);
        }
        return new CustomUserDetails(returnMember, oAuth2User.getAttributes());
    }
}
