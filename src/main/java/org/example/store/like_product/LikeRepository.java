package org.example.store.like_product;

import org.example.store.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<LikeProduct, Integer> {

    @Modifying
    @Query(value = "delete from like_product where id = :id", nativeQuery = true)
    int deleteById(int id);

    int countByProduct_ProductId(int productProductId);

    int countByLiker(Member liker);

    Optional<LikeProduct> findByLikerAndProduct_ProductId(Member liker, int productId);

    List<LikeProduct> findAllByLiker(Member liker);
}
