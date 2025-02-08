package org.example.store.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.store.chatRoom.ChatRoom;
import org.example.store.like_product.LikeProduct;
import org.example.store.member.entity.Member;
import org.example.store.memberReview.Review;
import org.example.store.payment.Payment;
import org.example.store.product.dto.ProductDto;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
@ToString
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int productId;

    @NotNull
    private String productName;

    @NotNull
    private String description;

    @NotNull
    private int price;

    @NotNull
    private String thumbnailUrl;

    @NotNull
    private String productStatus; //상품상태

    private int views;

    private String tag;

    private String category;

    private boolean display; //숨김 기능 구현

    @CreatedDate
    private LocalDateTime postDate;

    @LastModifiedDate
    private LocalDateTime updateDate;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member seller;

    @OneToMany(mappedBy = "product")
    private List<ChatRoom> chatRoomList;

    @OneToMany(mappedBy = "product")
    List<LikeProduct> likeList;

    @OneToOne(mappedBy = "product")
    private Review review;

    @OneToMany(mappedBy = "product")
    private List<Payment> paymentList;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Image image;

    @Builder
    public Product(int productId, int views, int price, boolean display,
                   String productName, String description, String tag, String category,
                   String productStatus, LocalDateTime postDate, LocalDateTime updateDate,
                   String thumbnailUrl, Member seller,
                   List<ChatRoom> chatRoomList, List<LikeProduct> likeList,
                   Review review, List<Payment> paymentList, Image image) {
        this.productId = productId;
        this.views = views;
        this.price = price;
        this.productName = productName;
        this.description = description;
        this.tag = tag;
        this.category = category;
        this.productStatus = productStatus;
        this.postDate = postDate;
        this.updateDate = updateDate;
        this.thumbnailUrl = thumbnailUrl;
        this.display = display;

        this.seller = seller;
        this.chatRoomList = chatRoomList;
        this.likeList = likeList;
        this.review = review;
        this.paymentList = paymentList;
        this.image = image;
    }

    public static ProductDto fromEntity(Product product) {
        return ProductDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .price(product.getPrice())
                .thumbnailUrl(product.getThumbnailUrl())
                .productStatus(product.getProductStatus())
                .tag(product.getTag())
                .category(product.getCategory())
                .views(product.getViews())
                .postDate(product.getPostDate())
                .display(product.isDisplay())
                .updateDate(product.getUpdateDate())
                //.imageDto(Image.fromEntity(product.getImage())) //image 는 fromEntity 에만 있음
                .seller(Member.fromEntity(product.getSeller()))
                .build();
    }

    public static List<ProductDto> fromEntityList(List<Product> productList) {
        List<ProductDto> productDtoList = new ArrayList<>();
        productList.forEach(product -> productDtoList.add(fromEntity(product)));
        return productDtoList;
    }
}
