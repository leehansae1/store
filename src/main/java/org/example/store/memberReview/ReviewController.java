package org.example.store.memberReview;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.dto.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰는 결제 성공 페이지에서 모달을 띄워 작성
    @PostMapping("/write/{productId}")
    public String writeReview(@ModelAttribute ReviewDto reviewDto, @PathVariable int productId,
                              @AuthenticationPrincipal CustomUserDetails user) {
        boolean isSaved = reviewService.writeReview(reviewDto, productId, user);
        if (isSaved) return "redirect:/chatRoom/" + productId;
        else return "/payment/success";
    }
}
