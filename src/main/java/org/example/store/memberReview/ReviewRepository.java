package org.example.store.memberReview;

import org.example.store.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    @Modifying
    @Query(value = "delete from MEMBER_REVIEW where reviewId = :reviewId", nativeQuery = true)
    int deleteById(int reviewId);

    List<Review> findAllBySeller(Member seller);
}
