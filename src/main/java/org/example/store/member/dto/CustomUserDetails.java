package com.dragontiger.prjectwave.member.dto;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.dragontiger.prjectwave.member.entity.Member;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CustomUserDetails implements UserDetails, OAuth2User {
  
  private final Member loggedMember; // 로그인된 사용자 정보

  private Map<String,Object> attributes;

  // 일반 로그인 방식
  public CustomUserDetails(Member loggedMember) {
    this.loggedMember = loggedMember;
  }

  // OAuth2 로그인 방식
  public CustomUserDetails(Member loggedMember, Map<String,Object> attributs) {
    this.loggedMember = loggedMember;
    this.attributes = attributs;
    
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public String getName() {
    return (String)attributes.get("name");
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
  Collection<GrantedAuthority> collection = new ArrayList<>();
    collection.add((GrantedAuthority)()-> String.valueOf(loggedMember.getRole()));
    return collection;
  }

  @Override
  public String getPassword() {
    return loggedMember.getUserPw(); // Member 클래스에서 getUserPw() 메서드를 호출
  }

  @Override
  public String getUsername() {
    return loggedMember.getUserId(); // Member 클래스에서 getUserId() 메서드를 호출
  }

}
