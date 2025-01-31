package org.example.store.product;

import jakarta.validation.constraints.NotNull;
import org.example.store.product.entity.Image;
import org.example.store.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findAllByDescriptionContainingIgnoreCaseOrProductNameContainingIgnoreCase(
            @NotNull String description, @NotNull String productName
    );

    Product image(Image image);
}
