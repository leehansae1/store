package org.example.store.memberReview;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.chatRoom.ChatRoomService;
import org.example.store.member.dto.CustomUserDetails;
import org.example.store.member.entity.Member;
import org.example.store.product.ProductService;
import org.example.store.product.entity.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final ProductService productService;

    private final ChatRoomService chatRoomService;

    public boolean writeReview(ReviewDto reviewDto, int productId, CustomUserDetails user) {
        log.info("Writing review");
        //리뷰하는 상품
        Product product = productService.getProduct(productId);
        reviewDto.setProductDto(Product.fromEntity(product));
        reviewDto.setSeller(Member.fromEntity(product.getSeller())); //판매자
        reviewDto.setReviewer(Member.fromEntity(user.getLoggedMember())); //구매자

        Review review = reviewRepository.save(ReviewDto.toEntity(reviewDto));

        chatRoomService.writePaymentResult(productId, user); //결제 완료 메시지를 채팅으로 전송
        return Review.fromEntity(review) != null;
    }

    // 상점 페이지에서 사용
    public List<ReviewDto> getReviewList(Member seller) {
        List<ReviewDto> reviewDtoList = new ArrayList<>();
        List<Review> reviewList = reviewRepository.findAllBySeller(seller);
        reviewList.forEach(review ->
                reviewDtoList.add(Review.fromEntity(review))
        );
        reviewDtoList.forEach(reviewDto -> {
            reviewDto.getReviewer().setUserPw("");
            reviewDto.getSeller().setUserPw("");
            reviewDto.getProductDto().getSeller().setUserPw("");
        });
        return reviewDtoList;
    }
}
