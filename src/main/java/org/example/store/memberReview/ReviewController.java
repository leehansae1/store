package org.example.store.memberReview;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    private final String prefix = "/review";

    @GetMapping("/write/{productId}")
    public String writeReview(Model model, @PathVariable String productId) {
        model.addAttribute("productId", productId);
        model.addAttribute("review", new ReviewDto());
        return prefix + "/write";
    }

    @PostMapping("/write/{userId}")
    //어센틱 어쩌구로 userId는 변경
    //productId 는 히든 인풋으로 넘김
    public String writeReview(ReviewDto reviewDto, int productId, @PathVariable String userId) {
        if (reviewService.writeReview(reviewDto, productId, userId)) return "/"; //어디로보낼지 고민중
        else return prefix + "/write";
    }

    // 상점 후기 보기
    // 어센틱 어쩌구로 변경
    @PostMapping("/list/{userId}")
    @ResponseBody
    public Map<String, Object> getReviewList(@PathVariable String userId, Model model) {
        List<ReviewDto> reviewDtoList = reviewService.getReviewList(userId);
        if (reviewDtoList != null) return Map.of("reviewList", reviewDtoList, "success", true);
        return Map.of("success", false);
    }

    // /review/list/{userId} 화면에서 버튼으로 처리 >> 남의 상점에 들어갔는데 내 후기가 있는 상황
    @DeleteMapping("/delete/{reviewId}")
    public Map<String, Boolean> deleteReview(@PathVariable int reviewId) {
        return reviewService.deleteReview(reviewId)
                ? Map.of("isDelete", true) : Map.of("isDelete", false);
    }

    // write 로 넘겨서 수정하게 만들기 >> 가능한 이유: id 가 포함이 되어있어서 update 쿼리로 나감
    @GetMapping("/modify/{reviewId}")
    public String modifyReview(@PathVariable int reviewId, RedirectAttributes redirectAttributes) {
        ReviewDto reviewDto = reviewService.getReview(reviewId);
        redirectAttributes.addAttribute("review", reviewDto);
        //if(isModify != null && isModify(==자체가 불린)) 이라면 페이지 타이틀, 문구 바꾸기
        redirectAttributes.addAttribute("isModify", true);
        return prefix + "redirect:" + prefix + "/write/" + reviewDto.getProductDto().getProductId();
    }
}
