package org.example.store.member.social;

public interface SocialUserInfo {
  // 소셜 로그인 사용자 정보들을 받아옴
   String getEmail();
   String getName();
   String getProvider();
   String getProviderId();
}
