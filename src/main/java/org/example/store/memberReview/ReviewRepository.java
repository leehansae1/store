package org.example.store.memberReview;

import org.example.store.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findAllBySeller(Member seller);

    @Query(value = "SELECT AVG(rating) FROM member_review WHERE seller_id = :sellerId", nativeQuery = true)
    String findAvgBySeller(String sellerId);
}
