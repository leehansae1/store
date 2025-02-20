package org.example.store.product.repository;

import jakarta.validation.constraints.NotNull;
import org.example.store.member.entity.Member;
import org.example.store.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findAllBySeller(Member seller);

    @Query("SELECT p FROM Product p " +
            "WHERE (p.description LIKE %:searchWord% " +
            "OR p.category LIKE %:searchWord% " +
            "OR p.tag LIKE %:searchWord% " +
            "OR p.productName LIKE %:searchWord%) " +
            "AND p.display = :display " +
            "ORDER BY p.postDate DESC")
    List<Product> findDisplayProducts(@NotNull String searchWord, boolean display);
}

