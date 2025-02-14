package org.example.store.follow;

import org.example.store.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Integer> {

    int countBySeller(Member seller);

    int deleteBySellerAndFollower(Member seller, Member follower);

    boolean existsBySellerAndFollower(Member seller, Member follower);

    List<Follow> findAllBySeller(Member seller);

    List<Follow> findAllByFollower(Member follower);
}
