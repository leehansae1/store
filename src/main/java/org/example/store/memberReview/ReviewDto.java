package org.example.store.memberReview;

import lombok.*;
import org.example.store.member.dto.MemberDto;
import org.example.store.product.dto.ProductDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewDto {

    private int reviewId;

    private String reviewText;

    private int rating;

    private LocalDateTime reviewDate;

    private ProductDto productDto; //이걸로 링크걸 것

    private MemberDto reviewer;

    private MemberDto seller;

    public static Review toEntity(ReviewDto reviewDto) {
        return Review.builder()
                .reviewDate(reviewDto.getReviewDate())
                .reviewId(reviewDto.getReviewId())
                .reviewText(reviewDto.getReviewText())
                .rating(reviewDto.getRating())
                .reviewer(MemberDto.toEntity(reviewDto.getReviewer()))
                .seller(MemberDto.toEntity(reviewDto.getSeller()))
                .product(ProductDto.toEntity(reviewDto.getProductDto()))
                .build();
    }

    public static List<Review> toEntityList(List<ReviewDto> reviewDtoList) {
        List<Review> reviews = new ArrayList<>();
        reviewDtoList.forEach(reviewDto -> reviews.add(toEntity(reviewDto)));
        return reviews;
    }
}
