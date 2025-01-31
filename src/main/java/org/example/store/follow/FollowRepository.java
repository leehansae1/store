package org.example.store.follow;

import org.example.store.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Integer> {

    @Modifying
    @Query(value = "delete from Follow where id = :id",nativeQuery = true)
    int deleteFollowById(int id);

    List<Follow> findAllBySeller_UserId(String sellerUserId);

    List<Follow> findAllByFollower_UserId(String followerUserId);

    int countBySeller(Member seller);

    Optional<Follow> findBySellerAndFollower(Member seller, Member member);
}
