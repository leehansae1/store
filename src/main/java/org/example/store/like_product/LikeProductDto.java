package org.example.store.like_product;

import lombok.*;
import org.example.store.member.MemberDto;
import org.example.store.product.dto.ProductDto;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeProductDto {

    private int id;

    private MemberDto liker;

    private ProductDto productDto;

    private LocalDateTime likeAt;

    public static LikeProduct toEntity(LikeProductDto dto) {
        return LikeProduct.builder()
                .id(dto.getId())
                .likeAt(dto.getLikeAt())

                .liker(MemberDto.toEntity(dto.getLiker()))
                //.product(dto.getProductDto())
                .build();
    }
}
