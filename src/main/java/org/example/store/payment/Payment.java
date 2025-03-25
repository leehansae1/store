package org.example.store.payment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.store.member.entity.Member;
import org.example.store.product.entity.Product;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int paymentId;

    private String orderId;

    private String orderName; //주문명

    private int totalAmount; //금액

    private String method; //결제방법

    private String requestedAt; //결제시작

    private String approvedAt; //승인시작

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Member customer;

    @ManyToOne
    @JoinColumn(name = "product_Id")
    private Product product;

    private int success;

    private String errorCode;

    private String errorMessage;

    @Builder
    public Payment(String orderId, String orderName, int totalAmount, int paymentId,
                   String method, String requestedAt, String approvedAt,
                   int success, Member customer, Product product, String errorMessage, String errorCode) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.orderName = orderName;
        this.totalAmount = totalAmount;
        this.method = method;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.success = success;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;

        this.product = product;
        this.customer = customer;
    }

    public static PaymentDto fromEntity(Payment payment) {
        return PaymentDto.builder()
                .orderId(payment.getOrderId())
                .orderName(payment.getOrderName())
                .totalAmount(payment.getTotalAmount())
                .method(payment.getMethod())
                .requestedAt(payment.getRequestedAt())
                .approvedAt(payment.getApprovedAt())
                .success(payment.getSuccess())
                .errorCode(payment.getErrorCode())
                .errorMessage(payment.getErrorMessage())
                .paymentId(payment.getPaymentId())

                .productDto(Product.fromEntity(payment.getProduct()))
                .customer(Member.fromEntity(payment.getCustomer()))
                .build();
    }

    public static List<PaymentDto> fromEntityList(List<Payment> payments) {
        List<PaymentDto> paymentDtos = new ArrayList<>();
        payments.forEach(payment -> paymentDtos.add(fromEntity(payment)));
        return paymentDtos;
    }
}
