package org.example.store.member.social;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class KakaoUserInfo implements SocialUserInfo{

  private final Map<String,Object> attribute;

  @Override
  public String getEmail() {
    Map<String,Object> kakao_account = (Map)attribute.get("kakao_account");
    return (String) kakao_account.get("email");
  }

  @Override
  public String getName() {
    Map<String,Object> properties = (Map)attribute.get("properties");
    return (String)properties.get("nickname");
  }

  @Override
  public String getProvider() {
    return "kakao";
  }

  @Override
  public String getProviderId() {
    return getProvider() + "_" + attribute.get("id");
  }
}
