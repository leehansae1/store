package org.example.store.like_product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.store.member.entity.Member;
import org.example.store.product.entity.Product;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Table(name = "like_product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @ManyToOne
    @JoinColumn(name = "liker_id")
    private Member liker;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Builder
    public LikeProduct(Member liker, Product product, int id) {
        this.liker = liker;
        this.product = product;
        this.id = id;
    }

    public static LikeProductDto fromEntity(LikeProduct likeProduct) {
        return LikeProductDto.builder()
                .id(likeProduct.getId())
                .productDto(Product.fromEntity(likeProduct.getProduct()))
                .liker(Member.fromEntity(likeProduct.getLiker()))
                .build();
    }

    public static List<LikeProductDto> fromEntityList(List<LikeProduct> likeList) {
        List<LikeProductDto> likeProductDtoList = new ArrayList<>();
        likeList.forEach(likeProduct -> likeProductDtoList.add(fromEntity(likeProduct)));
        return likeProductDtoList;
    }
}
