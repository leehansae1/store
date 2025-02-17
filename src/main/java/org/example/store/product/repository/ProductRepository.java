package org.example.store.product.repository;

import jakarta.validation.constraints.NotNull;
import org.example.store.member.entity.Member;
import org.example.store.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findAllBySeller(Member seller);

    List<Product> findAllByDescriptionContainingOrCategoryContainingOrTagContainingOrProductNameContainingAndDisplayOrderByPostDateDesc(
            @NotNull String description, String category, String tag, @NotNull String productName, boolean display
    );
}

