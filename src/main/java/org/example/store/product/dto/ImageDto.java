package org.example.store.product.dto;

import lombok.*;
import org.example.store.product.entity.Image;
import org.example.store.product.entity.Product;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageDto {

    private int id;

    private String image01;

    private String image02;

    private String image03;

    private String image04;

    private String image05;

    private String image06;

    private Product product;

    public static Image toEntity(ImageDto imageDto) {
        return null;
    }
}

