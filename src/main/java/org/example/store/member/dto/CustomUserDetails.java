package org.example.store.member.dto;

import lombok.Getter;
import lombok.ToString;
import org.example.store.member.constant.MemberStatus;
import org.example.store.member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Getter
@ToString
public class CustomUserDetails implements UserDetails, OAuth2User {

  private final Member loggedMember;
  private Map<String, Object> attributes;

  // 일반 로그인
  public CustomUserDetails(Member loggedMember) {
    this.loggedMember = loggedMember;
  }

  // OAuth2 로그인
  public CustomUserDetails(Member loggedMember, Map<String, Object> attributes) {
    this.loggedMember = loggedMember;
    this.attributes = attributes;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public String getName() {
    return attributes != null ? (String) attributes.get("name") : loggedMember.getUserId();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(() -> String.valueOf(loggedMember.getRole()));
    return authorities;
  }

  @Override
  public String getPassword() {
    return loggedMember.getUserPw();
  }

  @Override
  public String getUsername() {
    return loggedMember.getUserId();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return loggedMember.getStatus() == MemberStatus.STATUS_ACTIVE;
  }
}
