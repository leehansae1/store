package org.example.store.member.service;

import org.example.store.member.dto.ModifyDto;
import org.example.store.member.dto.SignupDto;
import org.example.store.member.entity.Member;

import java.util.List;

public interface IMemberService {
   Member signup(SignupDto signupDto);                   // 회원 가입 정보 dto
   Member modifiedMember(ModifyDto modifyDto);           // 회원 정보 수정 dto
   boolean deleteMember(String userId, String userPw);   // 회원 소프트삭제 dto
   List<Member> getDeletedMembers();                     // 삭제된 회원 목록 반환 (관리자용)
   Member findByUserId(String userId);                   // 특정 회원 조회 (ID 기준)
   ModifyDto getMemberById(String loginUserId);          // modify.html에 아이디값 주입
}
