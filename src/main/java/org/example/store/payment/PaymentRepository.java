package org.example.store.payment;

import org.example.store.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    List<Payment> findAllByCustomer(Member customer);

    List<Payment> findAllByProduct_Member(Member member1);

    List<Payment> findAllByCustomerOrProduct_Member(Member member1, Member member2);
}
