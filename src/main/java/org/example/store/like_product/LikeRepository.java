package org.example.store.like_product;

import org.example.store.member.Member;
import org.example.store.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<LikeProduct, Integer> {

    int countByLiker(Member liker);

    List<LikeProduct> findAllByLiker(Member liker);

    int deleteByProductAndLiker(Product product, Member liker);

    int countByProduct(Product product);

    boolean existsByLikerAndProduct(Member liker, Product product);
}
