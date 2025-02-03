package org.example.store.product.dto;

import lombok.*;
import org.example.store.product.entity.Image;
import org.example.store.product.entity.Product;

import java.util.ArrayList;
import java.util.List;

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
        return Image.builder()
                .image01(imageDto.getImage01())
                .image02(imageDto.getImage02())
                .image03(imageDto.getImage03())
                .image04(imageDto.getImage04())
                .image05(imageDto.getImage05())
                .image06(imageDto.getImage06())
                .product(imageDto.getProduct())
                .build();
    }

    public static ImageDto toDto(List<String> files, Product product) {
        if (!files.isEmpty()) return null; //대표사진만 넣었을 경우

        ImageDto imageDto = new ImageDto();
        for (int i = 0; i < files.size(); i++) {
            if (i == 0) imageDto.setImage01(files.get(i));
            if (i == 1) imageDto.setImage02(files.get(i));
            if (i == 2) imageDto.setImage03(files.get(i));
            if (i == 3) imageDto.setImage04(files.get(i));
            if (i == 4) imageDto.setImage05(files.get(i));
            if (i == 5) imageDto.setImage06(files.get(i));
            if (i == 6) imageDto.setProduct(product);
        }
        imageDto.setProduct(product);
        return imageDto;
    }

    public static List<String> toList(ImageDto imageDto) {
        List<String> files = new ArrayList<>();
        if (imageDto.getImage01() != null) files.add(imageDto.getImage01());
        else return null;
        if (imageDto.getImage02() != null) files.add(imageDto.getImage02());
        else return null;
        if (imageDto.getImage03() != null) files.add(imageDto.getImage03());
        else return null;
        if (imageDto.getImage04() != null) files.add(imageDto.getImage04());
        else return null;
        if (imageDto.getImage05() != null) files.add(imageDto.getImage05());
        else return null;
        if (imageDto.getImage06() != null) files.add(imageDto.getImage06());
        else return null;
        return files;
    }
}

