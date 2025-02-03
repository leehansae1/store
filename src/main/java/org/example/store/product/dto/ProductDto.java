package org.example.store.product.dto;

import lombok.*;
import org.example.store_project.chatRoom.ChatRoomDto;
import org.example.store_project.like_product.LikeProductDto;
import org.example.store_project.member.MemberDto;
import org.example.store_project.memberReview.ReviewDto;
import org.example.store_project.payment.PaymentDto;
import org.example.store_project.product.entity.Product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
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

    private boolean sellStatus; //판매상태

    private boolean display; //숨김 기능 구현


    private MemberDto seller;

    private ImageDto imageDto;

    private ReviewDto reviewDto;

    private PaymentDto paymentDto;

    private ChatRoomDto chatRoomDto;

    private List<LikeProductDto> likeDtoList;

    // 아래 속성들은 실제 테이블엔 없음
    private boolean likeState;

    private int likeCount;

    public static Product toEntity(ProductDto productDto) {
        return Product.builder()
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
                .sellStatus(productDto.isSellStatus())

                .image(ImageDto.toEntity(productDto.getImageDto()))

                .seller(MemberDto.toEntity(productDto.getSeller()))
                .chatRoom(ChatRoomDto.toEntity(productDto.getChatRoomDto()))
                .likeList(LikeProductDto.toEntityList(productDto.getLikeDtoList()))
                .review(ReviewDto.toEntity(productDto.getReviewDto()))
                .payment(PaymentDto.toEntity(productDto.getPaymentDto()))
                .build();
    }

    public static List<Product> toEntityList(List<ProductDto> productDtoList) {
        List<Product> productList = new ArrayList<>();
        productDtoList.forEach(productDto -> productList.add(toEntity(productDto)));
        return productList;
    }
}
