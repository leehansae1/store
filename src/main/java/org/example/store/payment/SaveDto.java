package org.example.store.payment;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class SaveDto {

    private String orderId;

    private String orderName;

    private int amount;

    private String customerEmail;

    //로그인 계정의 아이디
    private String customerId;

    private int productId;
}
