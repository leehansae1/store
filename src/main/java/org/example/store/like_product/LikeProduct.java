package org.example.store.like_product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.store.member.Member;
import org.example.store.product.entity.Product;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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

    @LastModifiedDate
    private LocalDateTime likeAt;

    @Builder
    public LikeProduct(Member liker, Product product, LocalDateTime likeAt, int id) {
        this.liker = liker;
        this.product = product;
        this.likeAt = likeAt;
        this.id = id;
    }

    public static LikeProductDto fromEntity(LikeProduct likeProduct){
        return LikeProductDto.builder()
                .likeAt(likeProduct.getLikeAt())
                .id(likeProduct.getId())
                //.productDto(likeProduct.getProduct())
                .liker(Member.fromEntity(likeProduct.getLiker()))
                .build();
    }
}
