package org.example.store.memberReview;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.Member;
import org.example.store.product.ProductService;
import org.example.store.product.entity.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final ProductService productService;

    public boolean writeReview(ReviewDto reviewDto, int productId, Member 내계정) {
        //리뷰하는 상품
        Product product = productService.getProduct(productId);
        reviewDto.setProductDto(Product.fromEntity(product));
        reviewDto.setSeller(Member.fromEntity(product.getSeller())); //판매자

        //구매자
        reviewDto.setReviewer(Member.fromEntity(내계정));

        Review review = reviewRepository.save(ReviewDto.toEntity(reviewDto));
        return Review.fromEntity(review) != null;
    }

    public List<ReviewDto> getReviewList(Member 내계정) {
        List<ReviewDto> reviewDtoList = new ArrayList<>();
        List<Review> reviewList = reviewRepository.findAllBySeller(내계정);
        reviewList.forEach(review ->
                reviewDtoList.add(Review.fromEntity(review))
        );
        return reviewDtoList;
    }

    public boolean deleteReview(int reviewId) {
        return reviewRepository.deleteById(reviewId) > 0;
    }

    public ReviewDto getReview(int reviewId) {
        Review review;
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isPresent()) {
            review = optionalReview.get();
            return Review.fromEntity(review);
        }
        else return null;
    }
}
