package org.example.store.payment;

import org.example.store.member.entity.Member;
import org.example.store.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    Optional<Payment> findByCustomerAndProduct_ProductId(Member customer, int productProductId);

    Optional<Payment> findByProduct_SellerAndProduct_ProductId(Member productSeller, int productProductId);

    List<Payment> findAllByCustomerAndSuccess(Member customer, int success);

    List<Payment> findAllByProduct_SellerAndSuccess(Member productSeller, int success);

    List<Payment> findAllByCustomerOrProduct_SellerAndSuccess(Member customer, Member productSeller, int success);

    List<Payment> findByProductAndSuccess(Product product, int success);
}
