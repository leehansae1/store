package org.example.store.member.social;

import lombok.RequiredArgsConstructor;
import java.util.Map;

@RequiredArgsConstructor
public class GoogleUserInfo implements SocialUserInfo {
  private final Map<String, Object> attribute;

  @Override
  public String getEmail() {
    return (String) attribute.get("email");
  }

  @Override
  public String getName() {
    return (String) attribute.get("name");
  }

  @Override
  public String getProvider() {
    return "google";
  }

  @Override
  public String getProviderId() {
    return getProvider() + "_" + attribute.get("sub");
  }

  @Override
  public String getImageUrl() {
    return (String) attribute.get("picture");
  }
}
