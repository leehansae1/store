package org.example.store.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.store.chatRoom.ChatRoom;
import org.example.store.like_product.LikeProduct;
import org.example.store.member.Member;
import org.example.store.memberReview.Review;
import org.example.store.payment.Payment;
import org.example.store.product.dto.ProductDto;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int productId;

    @NotNull
    private String productName;

    @NotNull
    private String description;

    @NotNull
    private String price;

    @NotNull
    private String thumbnail;

    @NotNull
    private String productStatus;

    private int views;

    @CreatedDate
    private LocalDateTime postDate;

    @LastModifiedDate
    private LocalDateTime modifyDate;

    private int display;

    private String sale_status;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany
    private List<ChatRoom> chatRoomList;

    @OneToMany List<LikeProduct> likeList;

    @OneToOne
    private Review review;

    @OneToOne
    private Payment payment;

    @OneToOne
    private Image image;

    @Builder
    public static ProductDto fromEntity(Product product) {
        return null;
    }
}
