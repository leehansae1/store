package org.example.store.payment;

import lombok.*;
import org.example.store.member.MemberDto;
import org.example.store.product.dto.ProductDto;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PaymentDto {

    private String orderId; //이게 pk가 되게꾼

    private String requestedAt;

    private String orderName;

    private String method;

    private String approvedAt;

    private int totalAmount;

    private MemberDto customer;

    private ProductDto productDto;

    // 성공했으면 1, 실패시 0
    private int success;

    private String errorCode;

    private String errorMessage;

    public static Payment toEntity(PaymentDto paymentDto) {
        return Payment.builder()
                .orderId(paymentDto.getOrderId())
                .requestedAt(paymentDto.getRequestedAt())
                .orderName(paymentDto.getOrderName())
                .method(paymentDto.getMethod())
                .approvedAt(paymentDto.getApprovedAt())
                .totalAmount(paymentDto.getTotalAmount())
                .success(paymentDto.getSuccess())
                .errorCode(paymentDto.getErrorCode())
                .errorMessage(paymentDto.getErrorMessage())

                .product(ProductDto.toEntity(paymentDto.getProductDto()))
                .customer(MemberDto.toEntity(paymentDto.getCustomer()))
                .build();
    }
}
