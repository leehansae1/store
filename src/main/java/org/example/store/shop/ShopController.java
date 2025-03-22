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
    @GetMapping({"/products", "/products/{randomId}"})
    public String getMyProducts(@AuthenticationPrincipal @Nullable Object principal,
                                @PathVariable(required = false) String randomId, Model model) {

        CustomUserDetails user;
        if (principal instanceof CustomUserDetails) user = ((CustomUserDetails) principal);
        else {
            user = null;
            log.info("비회원 접근이에요");
        }

        List<ProductDto> productDtoList;
        MemberDto memberDto;
        if (randomId != null) { //판매자의 상점이라면
            Member member = memberService.getMemberByRandomId(Integer.parseInt(randomId));
            memberDto = Member.fromEntity(member);
            int followCount = followService.getFollowCount(member);
            boolean followState;

            if (user != null) { //회원이라면
                followState = followService.isFollowed(member, user.getLoggedMember());
            } else followState = false;
            memberDto.setFollowCount(followCount);
            memberDto.setFollowState(followState);

            productDtoList = productService.getSellerProducts(member);
        } else { //내 상점이라면
            memberDto = Member.fromEntity(user.getLoggedMember());
            productDtoList = productService.getMyProducts(user);
        }
        memberDto.setUserPw("");
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
        log.info("userId {}", userId);
        List<ReviewDto> reviewDtoList = reviewService.getReviewList(memberService.getMember(userId));
        if (reviewDtoList.isEmpty()) log.info("후기가 없어용");
        else reviewDtoList.forEach(r -> {
            log.info("SHOP reviewDto: {}", r);
            r.getReviewer().setUserPw("");
            r.getSeller().setUserPw("");
            r.getProductDto().getSeller().setUserPw("");
        });
        return !reviewDtoList.isEmpty()
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
        return !productDtoList.isEmpty()
                ? Map.of("productList", productDtoList, "isList", true, "count", productDtoList.size())
                : Map.of("isList", false, "count", "0");
    }

    // 구독자
    @PostMapping("/followers/{randomId}")
    @ResponseBody
    public Map<String, Object> getAllFollowers(@PathVariable String randomId) {
        Member member = memberService.getMemberByRandomId(Integer.parseInt(randomId));
        List<MemberDto> memberList = followService.getAllFollowers(member);
        memberList.forEach(memberDto ->
                memberDto.setProductCount(productService.countBySeller(MemberDto.toEntity(memberDto)))
        );
        return memberList.isEmpty()
                ? Map.of("isEmpty", true, "count", "0")
                : Map.of("isEmpty", false, "memberList", memberList, "count", memberList.size());
    }

    // 내가 구독한 사람
    @PostMapping("/followings/{randomId}")
    @ResponseBody
    public Map<String, Object> getAllSeller(@PathVariable String randomId) {
        Member member = memberService.getMemberByRandomId(Integer.parseInt(randomId));
        List<MemberDto> memberList = followService.getAllSeller(member);
        memberList.forEach(memberDto ->
                memberDto.setProductCount(productService.countBySeller(MemberDto.toEntity(memberDto)))
        );
        return memberList.isEmpty()
                ? Map.of("isEmpty", true, "count", "0")
                : Map.of("isEmpty", false, "memberList", memberList, "count", memberList.size());
    }
}
