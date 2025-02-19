package org.example.store.faq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.constant.Role;
import org.example.store.member.dto.CustomUserDetails;
import org.example.store.member.entity.Member;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FaqService {

    private final FaqRepository faqRepository;

    // FAQ 페이지 접근
    public List<FaqDto> getFaqList() {
        List<Faq> faqList = faqRepository.findAll();
        return Faq.fromEntityList(faqList);
    }

    // 관리자가 FAQ 작성
    public FaqDto writeFaq(FaqDto faqDto, CustomUserDetails user) {
        Member admin = user.getLoggedMember(); // 관리자 정보 가져오기

        Faq faq = Faq.builder()
                .faqCategory(faqDto.getFaqCategory())
                .question(faqDto.getQuestion())
                .answer(faqDto.getAnswer())
                .faqViews(0) // 초기 조회수는 0
                .member(admin)
                .build();

        return Faq.fromEntity(faqRepository.save(faq));
    }

    // FAQ 수정 시 기존 글 긁어오기
    public FaqDto getFaq(int faqId) {
        Faq faq = faqRepository.findById(faqId).orElse(null);
        return faq == null ? null : Faq.fromEntity(faq);
    }

    // FAQ 삭제
    @Transactional
    public boolean deleteFaq(int faqId) {
        try {
            faqRepository.deleteById(faqId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            log.warn("삭제 실패: FAQ ID {}가 존재하지 않음", faqId);
            return false;
        }
    }

    // 클릭 시 조회수 올리기
    @Transactional
    public int addViews(int faqId, CustomUserDetails user) {
        // 현재 조회한 사용자가 관리자라면 조회수 증가시키지 않음
        if (user.getLoggedMember().getRole() == Role.ROLE_ADMIN) {
            return 0;
        }

        // 조회수 증가
        return faqRepository.increaseViewCount(faqId);
    }

}
