package org.example.store.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.entity.Member;
import org.example.store.member.service.MemberService;
import org.example.store.product.ProductService;
import org.example.store.product.entity.Product;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        } else { // 성공 시에만 판매완료로 상태변경
            Product.fromEntity(product).setSellStatus(true);
        }
        Payment payment = paymentRepository.save(PaymentDto.toEntity(paymentDto));
        return Payment.fromEntity(payment) != null;
    }

    // 결제실패내역 // 멤버는 커스텀디테일 유저
    public List<PaymentDto> getFailPaymentList(Member 내계정) {
        List<PaymentDto> paymentDtos = new ArrayList<>();
        List<Payment> payments = paymentRepository
                .findAllByCustomerOrProduct_SellerAndSuccess(내계정, 내계정, 0);
        payments.forEach(payment ->
                paymentDtos.add(Payment.fromEntity(payment))
        );
        return paymentDtos;
    }

    // 모든 구매내역 //멤버는 커스텀디테일 유저
    public List<PaymentDto> getBuyPayments(Member 내계정) {
        List<PaymentDto> paymentDtos = new ArrayList<>();
        List<Payment> payments = paymentRepository
                .findAllByCustomerAndSuccess(내계정, 1);
        payments.forEach(payment ->
                paymentDtos.add(Payment.fromEntity(payment))
        );
        return paymentDtos;
    }

    // 모든 판매내역 //멤버는 커스텀디테일 유저
    public List<PaymentDto> getSellPayments(Member 내계정) {
        List<PaymentDto> paymentDtos = new ArrayList<>();
        List<Payment> payments = paymentRepository
                .findAllByProduct_SellerAndSuccess(내계정, 0);
        payments.forEach(payment ->
                paymentDtos.add(Payment.fromEntity(payment))
        );
        return paymentDtos;
    }

    // 구매내역 하나만 필요할 때 >> 채팅창에서 or 구매내역에서 주문 내역 상세 확인할 때
    public Payment getBuyPayment(Member 내계정, int productId) {
        Optional<Payment> optionalPayment = paymentRepository
                .findByCustomerAndProduct_ProductId(내계정, productId);
        return optionalPayment.orElse(null);
    }

    // 판매내역 하나만 필요할 때 >> 채팅창에서 or 구매내역에서 주문 내역 상세 확인할 때
    public Payment getSellPayment(Member 내계정, int productId) {
        Optional<Payment> optionalPayment = paymentRepository
                .findByProduct_SellerAndProduct_ProductId(내계정, productId);
        return optionalPayment.orElse(null);
    }
}
