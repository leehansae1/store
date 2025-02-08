package org.example.store.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.store.product.dto.ImageDto;

import java.util.ArrayList;
import java.util.List;

// 썸네일 컬럼, 곧 대표사진 포함 올릴 수 있는 이미지는 7개로 제한
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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
    public Image(String image01, String image02, String image03,
                 String image04, String image05, String image06,
                 Product product) {
        this.image01 = image01;
        this.image02 = image02;
        this.image03 = image03;
        this.image04 = image04;
        this.image05 = image05;
        this.image06 = image06;
        this.product = product;
    }

    public static ImageDto fromEntity(Image image) {
        return ImageDto.builder()
                .id(image.getId())
                .image01(image.getImage01())
                .image02(image.getImage02())
                .image03(image.getImage03())
                .image04(image.getImage04())
                .image05(image.getImage05())
                .image06(image.getImage06())
                .productDto(Product.fromEntity(image.getProduct()))
                .build();
    }

    public static List<String> toStrList(Image image) {
        List<String> files = new ArrayList<>();
        if (image.getImage01() != null) files.add(image.getImage01());
        else {
            System.out.println("이미지가 없습니다 여기는 Image의 toStrList");
            return files;
        }
        if (image.getImage02() != null) files.add(image.getImage02());
        else return files;
        if (image.getImage03() != null) files.add(image.getImage03());
        else return files;
        if (image.getImage04() != null) files.add(image.getImage04());
        else return files;
        if (image.getImage05() != null) files.add(image.getImage05());
        else return files;
        if (image.getImage06() != null) files.add(image.getImage06());
        return files;
    }
}
