package com.dragontiger.prjectwave.member.social;

public interface SocialUserInfo {
  // 소셜 로그인 사용자 정보들을 받아옴
  public String getEmail();
  public String getName();
  public String getProvider();
  public String getProviderId();
}
