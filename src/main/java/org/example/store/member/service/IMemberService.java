package org.example.store.member.service;

import org.example.store.member.dto.ModifyDto;
import org.example.store.member.dto.SignupDto;
import org.example.store.member.entity.Member;

import java.util.List;

public interface IMemberService {
   Member signup(SignupDto signupDto);
   Member modifiedMember(ModifyDto modifyDto);
   boolean deleteMember(String userId, String userPw);
   List<Member> getDeletedMembers();
   Member findByUserId(String userId);
   ModifyDto getMemberById(String loginUserId);
}
