package org.example.store.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.store.product.dto.ImageDto;

import java.util.ArrayList;
import java.util.List;


// 썸네일 컬럼, 곧 대표사진 포함 올릴 수 있는 이미지는 7개로 제한
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
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

    public static ImageDto fromEntity(Image image) {
        return null;
    }
    public static List<String> fromImage(Image image) {
        List<String> imageList = new ArrayList<>();
        if (image.getImage01() != null) {
            imageList.add(image.getImage01());
            if (image.getImage02() != null) {
                imageList.add(image.getImage02());
                if (image.getImage03() != null) {
                    imageList.add(image.getImage03());
                    if (image.getImage04() != null) {
                        imageList.add(image.getImage04());
                        if (image.getImage05() != null) {
                            imageList.add(image.getImage05());
                            if (image.getImage06() != null) {
                                imageList.add(image.getImage06());
                            }
                        }
                    }
                }
            }
        }
        return imageList;
    }
}
