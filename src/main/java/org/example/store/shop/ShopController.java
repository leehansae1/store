package org.example.store.shop;

import lombok.RequiredArgsConstructor;
import org.example.store.follow.FollowService;
import org.example.store.like_product.LikeService;
import org.example.store.member.Member;
import org.example.store.member.MemberDto;
import org.example.store.member.MemberService;
import org.example.store.memberReview.ReviewDto;
import org.example.store.memberReview.ReviewService;
import org.example.store.product.ProductService;
import org.example.store.product.dto.ProductDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {

    private final MemberService memberService;

    private final ProductService productService;

    private final ReviewService reviewService;

    private final LikeService likeService;

    private final FollowService followService;

    // userId 를 어떻게 대체할 수 있을까 >> 7자리 난수 속성을 하나 만들자

    // 상품
    @PostMapping("/products/{userId}")
    public Map<String, Object> getSellerProducts(@PathVariable String userId) {
        Member member = memberService.getMember(userId);
        List<ProductDto> productDtoList = productService.getSellerProducts(member);
        return productDtoList != null
                ? Map.of("productList", productDtoList, "isList", true)
                : Map.of("isList", false);
    }

    // 상점후기
    @PostMapping("/reviews/{userId}")
    public Map<String, Object> getReviews(@PathVariable String userId) {
        Member member = memberService.getMember(userId);
        List<ReviewDto> reviewDtoList = reviewService.getReviewList(member);
        return reviewDtoList != null
                ? Map.of("reviewList", reviewDtoList, "isList", true)
                : Map.of("isList", false);
    }

    // 찜
    @PostMapping("/favorites/{userId}")
    public Map<String , Object> getFavorites(@PathVariable String userId){
        Member member = memberService.getMember(userId);
        List<ProductDto> productDtoList = likeService.getLikeProducts(member);
        return productDtoList != null
                ? Map.of("productList", productDtoList, "isList", true)
                : Map.of("isList", false);
    }

    // 구독자
    @PostMapping("/followers/{userId}")
    public List<MemberDto> getAllFollowers(@PathVariable String userId) {
        Member member = memberService.getMember(userId);
        return followService.getAllFollowers(member);
    }
    // 내가 구독한 사람
    @PostMapping("/followings/{userId}")
    public List<MemberDto> getAllSeller(@PathVariable String userId) {
        Member member = memberService.getMember(userId);
        return followService.getAllSeller(member);
    }
}
