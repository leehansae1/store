package org.example.store.member.service;

import java.util.List;

import org.example.store.member.dto.ModifyDto;
import org.example.store.member.dto.SignupDto;
import org.example.store.member.entity.Member;

public interface IMemberService {
  public Member signup(SignupDto signupDto);                  // 회원 가입 정보 dto
  public Member modifiedMember(ModifyDto modifyDto);          // 회원 정보 수정 dto
  public boolean deleteMember(String userId, String userPw);  // 회원 소프트삭제 dto
  public List<Member> getDeletedMembers();                    // 삭제된 회원 목록 반환 (관리자용)
  public Member findByUserId(String userId);                  // 특정 회원 조회 (ID 기준)
  public ModifyDto getMemberById(String loginUserId);         // modify.html에 아이디값 주입

}
