package org.example.store.member.repository;

import org.example.store.member.constant.MemberStatus;
import org.example.store.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
  Optional<Member> findByUserId(String userId);

  List<Member> findByStatus(MemberStatus status);

  boolean existsByUserId(String userId);

  Optional<Member> findByRandomId(int randomId);
}
