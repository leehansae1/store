package org.example.store.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.Member;
import org.example.store.member.MemberService;
import org.example.store.product.ProductService;
import org.example.store.product.entity.Product;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final MemberService memberService;

    private final ProductService productService;

    public boolean resultSave(PaymentDto paymentDto, SaveDto saveDto) {
        Member member = memberService.getMember(saveDto.getCustomerId());
        log.info("member : {}", member);
        paymentDto.setCustomer(Member.fromEntity(member));

        Product product = productService.getProduct(saveDto.getProductId());
        log.info("product : {}", product);
        paymentDto.setProductDto(Product.fromEntity(product));

        // 실패 시 담아야하는 것들
        if (paymentDto.getMethod() != null) {
            paymentDto.setOrderId(saveDto.getOrderId());
            paymentDto.setTotalAmount(saveDto.getAmount());
            paymentDto.setApprovedAt(LocalDateTime.now().toString());
        }

        Payment payment = paymentRepository.save(PaymentDto.toEntity(paymentDto));
        // entity >> dto
        return Payment.fromEntity(payment) != null;
    }

    //멤버는 커스텀디테일 유저
    public List<PaymentDto> getPayments(Member 내계정) {
        List<PaymentDto> paymentDtos = new ArrayList<>();

        List<Payment> payments = paymentRepository.findAllByCustomerOrProduct_Member(내계정,내계정);
        // entity List >> dto List
        payments.forEach(payment ->
                paymentDtos.add(Payment.fromEntity(payment))
        );
        //페이먼츠 리스트를 페이먼츠 디티오 리스트로 담아서 리턴
        return paymentDtos;
    }

    //멤버는 커스텀디테일 유저 // 구매내역
    public List<PaymentDto> getBuyPayments(Member 내계정) {
        List<PaymentDto> paymentDtos = new ArrayList<>();

        List<Payment> payments = paymentRepository.findAllByCustomer(내계정);
        // entity List >> dto List
        payments.forEach(payment ->
                paymentDtos.add(Payment.fromEntity(payment))
        );
        return paymentDtos;
    }

    //멤버는 커스텀디테일 유저 // 판매내역
    public List<PaymentDto> getSellPayments(Member member) {
        List<PaymentDto> paymentDtos = new ArrayList<>();
        //나중에 커스텀디테일 유저로 바꿈
        Member member1 = memberService.getMember(member.getUserId());

        List<Payment> payments = paymentRepository.findAllByProduct_Member(member1);
        // entity List >> dto List
        payments.forEach(payment ->
                paymentDtos.add(Payment.fromEntity(payment))
        );
        return paymentDtos;
    }
}
