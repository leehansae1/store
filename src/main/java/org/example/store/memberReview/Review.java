package org.example.store.memberReview;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.store.member.entity.Member;
import org.example.store.product.entity.Product;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "MEMBER_REVIEW")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int reviewId;

    private String reviewText;

    private int rating;

    @CreatedDate
    private LocalDateTime reviewDate;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private Member reviewer;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Member seller;

    @Builder
    public Review(int reviewId, String reviewText, int rating, LocalDateTime reviewDate,
                  Member reviewer, Member seller, Product product) {
        this.reviewId = reviewId;
        this.reviewText = reviewText;
        this.rating = rating;
        this.reviewDate = reviewDate;
        this.reviewer = reviewer;
        this.seller = seller;
        this.product = product;
    }

    public static ReviewDto fromEntity(Review review) {
        return ReviewDto.builder()
                .reviewDate(review.getReviewDate())
                .reviewId(review.getReviewId())
                .reviewText(review.getReviewText())
                .rating(review.getRating())
                .reviewer(Member.fromEntity(review.getReviewer()))
                .seller(Member.fromEntity(review.getSeller()))
                .productDto(Product.fromEntity(review.getProduct()))
                .build();
    }

    public static List<ReviewDto> fromEntityList(List<Review> reviews) {
        List<ReviewDto> reviewDtoList = new ArrayList<>();
        reviews.forEach(review -> reviewDtoList.add(fromEntity(review)));
        return reviewDtoList;
    }
}
