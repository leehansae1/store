package org.example.store.shop;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.follow.FollowService;
import org.example.store.like_product.LikeService;
import org.example.store.member.dto.CustomUserDetails;
import org.example.store.member.entity.Member;
import org.example.store.member.dto.MemberDto;
import org.example.store.member.service.MemberService;
import org.example.store.memberReview.ReviewDto;
import org.example.store.memberReview.ReviewService;
import org.example.store.product.ProductService;
import org.example.store.product.dto.ProductDto;
import org.example.store.product.entity.Product;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/shop")
@RequiredArgsConstructor
@Slf4j
public class ShopController {

    private final MemberService memberService;

    private final ProductService productService;

    private final ReviewService reviewService;

    private final LikeService likeService;

    private final FollowService followService;

    // 바로 보이는 내상점 페이지 >> 내 상품 리스트 나열
    @GetMapping({"/products", "/products/{productId}"})
    public String getMyProducts(@AuthenticationPrincipal @Nullable Object principal,
                                @PathVariable(required = false) Integer productId, Model model) {
        CustomUserDetails user = null;
        if (principal instanceof CustomUserDetails) user = ((CustomUserDetails) principal);
        else log.info("비회원 접근이에요");

        List<ProductDto> productDtoList;
        MemberDto memberDto;
        if (productId != null) { //판매자의 상점이라면
            Product product = productService.getProduct(productId);
            memberDto = Member.fromEntity(product.getSeller());
            int followCount = followService.getFollowCount(product.getSeller());
            boolean followState;
            if (user != null) { //회원이라면
                followState = followService.isFollowed(product.getSeller(), user.getLoggedMember());
            } else followState = false;
            memberDto.setFollowCount(followCount);
            memberDto.setFollowState(followState);

            productDtoList = productService.getSellerProducts(product.getSeller());
        } else { //내 상점이라면
            memberDto = Member.fromEntity(user.getLoggedMember());
            productDtoList = productService.getMyProducts(user);
        }
        model.addAttribute("member", memberDto);
        model.addAttribute("productList", productDtoList);
        // 공통
        if (productDtoList.isEmpty()) return "shop/products";
        int sellCount = productService.getSellTotalCount(productDtoList.getFirst().getSeller());
        model.addAttribute("sellCount", sellCount);

        return "shop/products";
    }

    // 상점후기
    @PostMapping("/reviews/{userId}")
    @ResponseBody
    public Map<String, Object> getReviews(@PathVariable String userId) {
        List<ReviewDto> reviewDtoList = reviewService.getReviewList(memberService.getMember(userId));
        if (reviewDtoList.isEmpty()) log.info("후기가 없어용");
        else reviewDtoList.forEach(r -> log.info("SHOP reviewDto: {}", r));
        return reviewDtoList.isEmpty()
                ? Map.of("reviewList", reviewDtoList, "isList", true)
                : Map.of("isList", false);
    }

    // 찜 >> 내 상점에서만 조회 가능
    @PostMapping("/favorites")
    @ResponseBody
    public Map<String, Object> getFavorites(@AuthenticationPrincipal CustomUserDetails customUser) {
        List<ProductDto> productDtoList = likeService.getLikeProducts(customUser);
        if (productDtoList.isEmpty()) log.info("찜한 상품이 없어요");
        else productDtoList.forEach(productDto -> log.info("SHOP productDto: {}", productDto));
        return productDtoList.isEmpty()
                ? Map.of("productList", productDtoList, "isList", true)
                : Map.of("isList", false);
    }

    // 구독자
    @PostMapping("/followers/{userId}")
    @ResponseBody
    public List<MemberDto> getAllFollowers(@PathVariable String userId) {
        Member member = memberService.getMember(userId);
        return followService.getAllFollowers(member);
    }

    // 내가 구독한 사람
    @PostMapping("/followings/{userId}")
    @ResponseBody
    public List<MemberDto> getAllSeller(@PathVariable String userId) {
        Member member = memberService.getMember(userId);
        return followService.getAllSeller(member);
    }
}
