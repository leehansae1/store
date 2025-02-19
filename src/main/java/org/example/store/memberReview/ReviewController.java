package org.example.store.memberReview;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.dto.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    private final String prefix = "review";

    @GetMapping("/write/{productId}")
    public String writeReview(Model model, @PathVariable String productId) {
        model.addAttribute("productId", productId);
        model.addAttribute("review", new ReviewDto());
        return prefix + "/write";
    }

    //productId 는 히든 인풋으로 넘김
    @PostMapping("/write")
    public String writeReview(ReviewDto reviewDto, int productId,
                              @AuthenticationPrincipal CustomUserDetails user) {
        return reviewService.writeReview(reviewDto, productId, user) ?
                "chatRoom/paymentResult/" + productId : "review/write";
    }



    // /review/list/{userId} 화면에서 버튼으로 처리 >> 남의 상점에 내 후기가 달려있고, 거기서 삭제 버튼
    @DeleteMapping("/delete/{reviewId}")
    public Map<String, Boolean> deleteReview(@PathVariable int reviewId) {
        return reviewService.deleteReview(reviewId)
                ? Map.of("isDelete", true)
                : Map.of("isDelete", false);
    }

    // write 로 넘겨서 수정하게 만들기 >> 가능한 이유: id 가 포함이 되어있어서 update 쿼리로 나감
    @GetMapping("/modify/{reviewId}")
    public String modifyReview(@PathVariable int reviewId,
                               RedirectAttributes redirectAttributes) {
        ReviewDto reviewDto = reviewService.getReview(reviewId);
        redirectAttributes.addAttribute("review", reviewDto);
        //if(isModify != null && isModify(==자체가 불린)) 이라면 페이지 타이틀, 문구 바꾸기
        redirectAttributes.addAttribute("isModify", true);
        return "redirect:" + prefix + "/write/" + reviewDto.getProductDto().getProductId();
    }
}
