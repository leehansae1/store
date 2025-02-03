package org.example.store.member.social;

import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GoogleUserInfo implements SocialUserInfo{

  // 소셜 로그인 정보 (구글에서 받아온 사용자 정보)
  private final Map<String,Object> attribute;

  // 이메일, 사용자 이름, 로그인 제공자 이름, ID를 반환

  @Override
  public String getEmail() {
    return (String)attribute.get("eamil");
  }

  @Override
  public String getName() {
    return (String)attribute.get("name");
  }

  @Override
  public String getProvider() {
    return "google";
  }

  @Override
  public String getProviderId() {
    return getProvider() + "_" + attribute.get("sub");
  }

}
