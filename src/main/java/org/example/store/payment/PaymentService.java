package org.example.store.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.dto.CustomUserDetails;
import org.example.store.member.entity.Member;
import org.example.store.member.service.MemberService;
import org.example.store.product.ProductService;
import org.example.store.product.dto.ProductDto;
import org.example.store.util.DateUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
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
        } else {
            boolean isSell = productService.toggleDisplay(productDto.getProductId(),true); //판매완료, 상품숨기기
            if (isSell) log.info("숨김처리 완료");
        }
        Payment payment = paymentRepository.save(PaymentDto.toEntity(paymentDto));
        return Payment.fromEntity(payment) != null;
    }

    // 모든 구매내역
    public List<PaymentDto> getBuyPayments(CustomUserDetails user) {
        List<PaymentDto> paymentDtos = new ArrayList<>();
        List<Payment> payments = paymentRepository
                .findAllByCustomerOrderByPaymentIdDesc(user.getLoggedMember());
        payments.forEach(payment ->
                paymentDtos.add(Payment.fromEntity(payment))
        );
        paymentDtos.forEach(paymentDto -> {
            // LocalDateTime으로 변경
            String target = paymentDto.getApprovedAt() != null
                    ? paymentDto.getApprovedAt()
                    : paymentDto.getRequestedAt();
            LocalDateTime ldt;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            if (target.contains("T")){
                ldt = OffsetDateTime.parse(target).toLocalDateTime();
            } else ldt = LocalDateTime.parse(target, formatter);
            paymentDto.setBuyTime(ldt);

            paymentDto.getCustomer().setUserPw("");
            paymentDto.getProductDto().getSeller().setUserPw("");

            log.info("paymentDto == {}", paymentDto);
        });
        return paymentDtos;
    }
}
