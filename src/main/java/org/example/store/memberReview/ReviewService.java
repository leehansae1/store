package org.example.store.memberReview;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.Member;
import org.example.store.member.MemberService;
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

    private final MemberService memberService;

    public boolean writeReview(ReviewDto reviewDto, int productId, String userId) {
        //리뷰하는 상품
        Product product = productService.getProduct(productId);
        reviewDto.setProductDto(Product.fromEntity(product));
        reviewDto.setSeller(Member.fromEntity(product.getMember())); //판매자

        //구매자
        Member member = memberService.getMember(userId);
        reviewDto.setReviewer(Member.fromEntity(member));

        Review review = reviewRepository.save(ReviewDto.toEntity(reviewDto));
        return Review.fromEntity(review) != null;
    }

    public List<ReviewDto> getReviewList(String userId) {
        List<ReviewDto> reviewDtoList = new ArrayList<>();

        List<Review> reviewList = reviewRepository.findAllBySeller_UserId(userId);
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
