package org.example.store.faq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.entity.Member;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

// 에러항목을 정해서 빈칸이면 들어갈 수 없게 자바스크립으로 제어
@RequestMapping("/faq")
@RequiredArgsConstructor
@Slf4j
@Controller
public class FaqController {

    private final FaqService faqService;

    private final String prefix = "/faq";

    // 클라이언트-어드민 모두 리스트로 조회
    // 따로 항목마다 보는 페이지는 만들지 않을 예정 >> 추후 변경될 수 있음
    @GetMapping("/list")
    public String list(Model model) {
        List<FaqDto> faqDtoList = faqService.getFaqList();
        model.addAttribute("faqDtoList", faqDtoList);
        faqDtoList.forEach(faqDto ->
                System.out.println("faqDto의 멤버디티오의 faqDto리스트의 개수 만큼 찍힙니다")
        );
        return prefix + "/list";
    }

    // 관리자가 문답 추가
    @GetMapping("/write")
    public String write(Model model) {
        model.addAttribute("faqList", new FaqDto());
        return prefix + "/write";
    }

    // 내계정 >> 어센틱 어쩌구로 바꾸기
    @PostMapping("/write")
    public String write(FaqDto faqDto) {
        Member 내계정 = null;
        FaqDto resultDto = faqService.writeFaq(faqDto, 내계정);
        if (resultDto != null) return prefix + "/list";
        return prefix + "/write";
    }

    // 관리자가 문답 수정
    @GetMapping("/modify/{faqId}")
    public String modify(@PathVariable int faqId, RedirectAttributes redirectAttributes) {
        FaqDto faqDto = faqService.getFaq(faqId);
        redirectAttributes.addAttribute("faqDto", faqDto);
        //if(isModify != null && isModify(==자체가 불린)) 이라면 페이지 타이틀, 문구 바꾸기
        redirectAttributes.addAttribute("isModify", true);
        log.info("컨트롤러의 faqDto == {}", faqDto);
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
    @PostMapping("/addViews/{faqId}")
    public Map<String, Object> addViews(@PathVariable int faqId) {
        Member 내계정 = null;
        int views = faqService.addViews(faqId, 내계정);

        return views > 0
                ? Map.of("success", true, "views", views)
                : Map.of("success", false);
    }
}
