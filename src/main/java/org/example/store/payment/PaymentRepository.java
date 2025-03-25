package org.example.store.payment;

import org.example.store.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    List<Payment> findAllByCustomerOrderByPaymentIdDesc(Member customer);
}
