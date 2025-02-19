package org.example.store.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.dto.CustomUserDetails;
import org.example.store.member.entity.Member;
import org.example.store.member.service.MemberService;
import org.example.store.product.ProductService;
import org.example.store.product.dto.ProductDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        paymentDto.setCustomer(Member.fromEntity(member));

        ProductDto productDto = productService.getProductDto(saveDto.getProductId());
        paymentDto.setProductDto(productDto);
        paymentDto.setOrderId(saveDto.getOrderId());
        // 실패 시 담아야하는 것들
        if (paymentDto.getSuccess() < 1) {
            paymentDto.setOrderName(saveDto.getOrderName());
            paymentDto.setTotalAmount(saveDto.getAmount());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            paymentDto.setApprovedAt(LocalDateTime.now().format(formatter));
        } else productService.hideProduct(productDto.getProductId()); //product 판매완료, true로 바꿔주기
        Payment payment = paymentRepository.save(PaymentDto.toEntity(paymentDto));
        return Payment.fromEntity(payment) != null;
    }

    // 결제실패내역
    public List<PaymentDto> getFailPaymentList(CustomUserDetails user) {
        List<PaymentDto> paymentDtos = new ArrayList<>();
        List<Payment> payments = paymentRepository
                .findAllByCustomerOrProduct_SellerAndSuccess(
                        user.getLoggedMember(), user.getLoggedMember(), 0
                );
        payments.forEach(payment ->
                paymentDtos.add(Payment.fromEntity(payment))
        );
        return paymentDtos;
    }

    // 모든 구매내역
    public List<PaymentDto> getBuyPayments(CustomUserDetails user) {
        List<PaymentDto> paymentDtos = new ArrayList<>();
        List<Payment> payments = paymentRepository
                .findAllByCustomerAndSuccess(user.getLoggedMember(), 1);
        payments.forEach(payment ->
                paymentDtos.add(Payment.fromEntity(payment))
        );
        return paymentDtos;
    }

    // 모든 판매내역 //멤버는 커스텀디테일 유저
    public List<PaymentDto> getSellPayments(CustomUserDetails user) {
        List<PaymentDto> paymentDtos = new ArrayList<>();
        List<Payment> payments = paymentRepository
                .findAllByProduct_SellerAndSuccess(user.getLoggedMember(), 0);
        payments.forEach(payment ->
                paymentDtos.add(Payment.fromEntity(payment))
        );
        return paymentDtos;
    }

    // 구매내역 하나만 필요할 때 >> 채팅창에서 or 구매내역에서 주문 내역 상세 확인할 때
    public Payment getBuyPayment(CustomUserDetails user, int productId) {
        Optional<Payment> optionalPayment = paymentRepository
                .findByCustomerAndProduct_ProductId(user.getLoggedMember(), productId);
        return optionalPayment.orElse(null);
    }

    // 판매내역 하나만 필요할 때 >> 채팅창에서 or 구매내역에서 주문 내역 상세 확인할 때
    public Payment getSellPayment(CustomUserDetails user, int productId) {
        Optional<Payment> optionalPayment = paymentRepository
                .findByProduct_SellerAndProduct_ProductId(user.getLoggedMember(), productId);
        return optionalPayment.orElse(null);
    }
}
