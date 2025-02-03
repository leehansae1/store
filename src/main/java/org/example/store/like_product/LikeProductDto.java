package org.example.store.like_product;

import lombok.*;
import org.example.store.member.MemberDto;
import org.example.store.product.dto.ProductDto;

import java.util.ArrayList;
import java.util.List;

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

    public static LikeProduct toEntity(LikeProductDto dto) {
        return LikeProduct.builder()
                .id(dto.getId())

                .liker(MemberDto.toEntity(dto.getLiker()))
                .product(ProductDto.toEntity(dto.getProductDto()))
                .build();
    }
    public static List<LikeProduct> toEntityList(List<LikeProductDto> likeDtoList) {
        List<LikeProduct> likeProductList = new ArrayList<>();
        likeDtoList.forEach(likeDto -> likeProductList.add(toEntity(likeDto)));
        return likeProductList;
    }
}
