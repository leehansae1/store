package org.example.store.faq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.Member;
import org.example.store.member.MemberService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FaqService {

    private final FaqRepository faqRepository;

    private final MemberService memberService;

    public List<FaqDto> getFaqList() {
        List<Faq> faqList = faqRepository.findAll();
        List<FaqDto> faqDtoList = new ArrayList<>();

        faqList.forEach(faq -> {
                    FaqDto faqDto = FaqDto.builder()
                            .memberDto(Member.fromEntity(faq.getMember()))
                            .faqId(faq.getFaqId())
                            .faqCategory(faq.getFaqCategory())
                            .question(faq.getQuestion())
                            .answer(faq.getAnswer())
                            .faqViews(faq.getFaqViews())
                            .build();
                    faqDtoList.add(faqDto);
                }
        );
        return faqDtoList;
    }

    // 관리자가 자주묻는 질문을 작성한다
    public FaqDto writeFaq(FaqDto faqDto, String adminId) {
        Member member = memberService.getMember(adminId);
        faqDto.setMemberDto(Member.fromEntity(member));
        Faq resultFaq = faqRepository.save(FaqDto.toEntity(faqDto));
        return Faq.fromEntity(resultFaq);
    }

    // 문답 수정 시 데이터 긁어오기
    public FaqDto getFaq(int faqId) {
        Faq faq = faqRepository.findById(faqId).orElse(null);
        log.info("서비스의 faq == {}", faq);
        if (faq == null) return null;
        return Faq.fromEntity(faq);
    }

    public boolean deleteFaq(int faqId) {
        faqRepository.deleteById(faqId);
        // db에 해당 데이터가 존재하지 않으면 true
        return !faqRepository.existsById(faqId);
    }

    public int addViews(int faqId, String adminId) {
        Member member = memberService.getMember(adminId);
        if (member.getRole().equals("role_admin")) return 0;

        FaqDto faqDto;
        Optional<Faq> optionalFaq = faqRepository.findById(faqId);
        if (optionalFaq.isPresent()) {
            faqDto = Faq.fromEntity(optionalFaq.get());
            faqDto.setFaqViews(faqDto.getFaqViews() + 1);
        } else return -1; //admin 이면 0, faq 가 없으면 -1 반환
        return faqRepository.save(FaqDto.toEntity(faqDto)).getFaqViews();
    }
}
