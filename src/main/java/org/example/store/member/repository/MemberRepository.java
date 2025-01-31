package com.dragontiger.prjectwave.member.repository;

import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dragontiger.prjectwave.member.constant.MemberStatus;
import com.dragontiger.prjectwave.member.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member,String>{
  Optional<Member> findByUserId(String userId);

  // 관리자용 : 삭제 상태의 회원 조회
  List<Member> findByStatus(MemberStatus status);
}
