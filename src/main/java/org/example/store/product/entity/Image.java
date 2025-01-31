package org.example.store.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.example.store.product.dto.ImageDto;


// 썸네일 컬럼, 곧 대표사진 포함 올릴 수 있는 이미지는 7개로 제한
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    private String image01;

    private String image02;

    private String image03;

    private String image04;

    private String image05;

    private String image06;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Builder
    public ImageDto fromEntity(Image image) {
        return null;
    }
}
