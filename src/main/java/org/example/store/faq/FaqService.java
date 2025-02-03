package org.example.store.faq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.entity.Member;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FaqService {

    private final FaqRepository faqRepository;

    // FAQ 페이지 접근
    public List<FaqDto> getFaqList() {
        List<Faq> faqList = faqRepository.findAll();
        List<FaqDto> faqDtoList = new ArrayList<>();

        faqList.forEach(faq -> faqDtoList.add(Faq.fromEntity(faq)));
        return faqDtoList;
    }

    // 관리자가 FAQ 작성
    public FaqDto writeFaq(FaqDto faqDto, Member 내계정) {
        faqDto.setMemberDto(Member.fromEntity(내계정));
        Faq resultFaq = faqRepository.save(FaqDto.toEntity(faqDto));
        return Faq.fromEntity(resultFaq);
    }

    // FAQ 수정 시 기존 글 긁어오기
    public FaqDto getFaq(int faqId) {
        Faq faq = faqRepository.findById(faqId).orElse(null);
        return faq == null ? null : Faq.fromEntity(faq);
    }

    // FAQ 삭제
    public boolean deleteFaq(int faqId) {
        faqRepository.deleteById(faqId);
        return !faqRepository.existsById(faqId);
    }

    // 조회수 올리기
    public int addViews(int faqId, Member 내계정) {
//        if (내계정.getRole().equals("role_admin")) return 0;
        FaqDto faqDto;
        Optional<Faq> optionalFaq = faqRepository.findById(faqId);
        if (optionalFaq.isPresent()) {
            faqDto = Faq.fromEntity(optionalFaq.get());
            faqDto.setFaqViews(faqDto.getFaqViews() + 1);
            return faqRepository.save(FaqDto.toEntity(faqDto)).getFaqViews();
        } else return -1; // FAQ 질문 접근자가 admin 이면 0, faq 가 없으면 -1 반환
    }
}
