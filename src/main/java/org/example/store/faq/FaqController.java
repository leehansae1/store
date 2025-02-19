package org.example.store.faq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.dto.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@RequestMapping("/faq")
@RequiredArgsConstructor
@Slf4j
@Controller
public class FaqController {

    private final FaqService faqService;

    private final String prefix = "faq";

    // 클라이언트-어드민 모두 리스트로 조회
    @GetMapping("/list")
    public String list(Model model) {
        List<FaqDto> faqDtoList = faqService.getFaqList();
        model.addAttribute("faqDtoList", faqDtoList);
        faqDtoList.forEach(faqDto -> {
            System.out.println("faqDto의 멤버디티오의 faqDto리스트의 개수 만큼 찍힙니다");
            log.info("faq Dto ==== {}", faqDto);
        });
        return prefix + "/list";
    }

    // 관리자가 문답 작성
    @GetMapping("/write")
    public String write(Model model) {
        model.addAttribute("faqList", new FaqDto());
        return prefix + "/write";
    }

    // 작성 된 문답 저장
    @PostMapping("/write")
    public String write(FaqDto faqDto, @AuthenticationPrincipal CustomUserDetails user
                        //밸리데이션 추가 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    ) {
        FaqDto resultDto = faqService.writeFaq(faqDto, user);
        if (resultDto != null) return prefix + "/list";
        return prefix + "/write";
    }

    // 관리자가 문답 수정
    @GetMapping("/modify/{faqId}")
    public String modify(@PathVariable int faqId, RedirectAttributes redirectAttributes) {
        FaqDto faqDto = faqService.getFaq(faqId);
        log.info("컨트롤러의 faqDto == {}", faqDto);
        redirectAttributes.addFlashAttribute("faqDto", faqDto);
        // if(isModify != null && isModify(==자체가 불린)) 이라면 페이지 타이틀, 문구 바꾸기
        redirectAttributes.addFlashAttribute("isModify", true);
        return "redirect:" + prefix + "/write";
    }

    // 관리자가 삭제
    @DeleteMapping("/delete/{faqId}")
    @ResponseBody
    public Map<String, Boolean> delete(@PathVariable int faqId) {
        return faqService.deleteFaq(faqId)
                ? Map.of("success", true) : Map.of("success", false);
    }

    // ajax 처리 = ex) question 컬럼에 해당하는 곳에 data-id 값을 faqId로 설정
    // 로그인 사용자를 넣은 이유는 관리자라면 조회수 변동없게 하기 위함
    @PostMapping("/addViews/{faqId}")
    @ResponseBody
    public Map<String, Object> addViews(@PathVariable int faqId,
                                        @AuthenticationPrincipal CustomUserDetails user) {
        int views = faqService.addViews(faqId, user);
        return views > 0
                ? Map.of("success", true, "views", views)
                : Map.of("success", false);
    }
}
