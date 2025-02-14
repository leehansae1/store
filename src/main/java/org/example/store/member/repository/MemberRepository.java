package org.example.store.member.repository;

import org.example.store.member.constant.MemberStatus;
import org.example.store.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,String>{
  // 아이디로 회원 정보 조회
  Optional<Member> findByUserId(String userId);

  // 아이디 존재 여부 확인
  boolean existsByUserId(String userId);

  // 관리자용 : 삭제 상태의 회원 조회
  List<Member> findByStatus(MemberStatus status);

}
