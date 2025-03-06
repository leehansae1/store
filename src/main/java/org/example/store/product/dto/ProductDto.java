package org.example.store.product.dto;

import lombok.*;
import org.example.store.chatRoom.ChatRoomDto;
import org.example.store.like_product.LikeProductDto;
import org.example.store.member.dto.MemberDto;
import org.example.store.memberReview.ReviewDto;
import org.example.store.payment.PaymentDto;
import org.example.store.product.entity.Product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private int productId;

    private String productName;

    private String description;

    private int price;

    private String thumbnailUrl;

    private String productStatus; //상품상태

    private int views;

    private LocalDateTime postDate;

    private String tag;

    private String category;

    private boolean display; //숨김 기능 구현

    private MemberDto seller;

    private ImageDto imageDto;

    private ReviewDto reviewDto;

    private PaymentDto paymentDto;

    private List<ChatRoomDto> chatRoomDtoList;

    private List<LikeProductDto> likeDtoList;

    private boolean isSell;

    // 아래 속성들은 실제 테이블엔 없음
    private boolean likeState;

    private int likeCount;

    private String timeAgo;

    public static Product toEntity(ProductDto productDto) {
        return Product.builder()
                .isSell(productDto.isSell())
                .productId(productDto.getProductId())
                .productName(productDto.getProductName())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .thumbnailUrl(productDto.getThumbnailUrl())
                .productStatus(productDto.getProductStatus())
                .views(productDto.getViews())
                .postDate(productDto.getPostDate())
                .tag(productDto.getTag())
                .category(productDto.getCategory())
                .display(productDto.isDisplay())

                .seller(MemberDto.toEntity(productDto.getSeller()))
                .build();
    }

    public static List<Product> toEntityList(List<ProductDto> productDtoList) {
        List<Product> productList = new ArrayList<>();
        productDtoList.forEach(productDto -> productList.add(toEntity(productDto)));
        return productList;
    }
}
