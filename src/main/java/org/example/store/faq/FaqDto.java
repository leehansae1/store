package org.example.store.faq;

import lombok.*;
import org.example.store.member.dto.MemberDto;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Setter
public class FaqDto {

    private int faqId;

    private String faqCategory;

    private String question;

    private String answer;

    private int faqViews;

    // 작성한 관리자 색출 아이디
    private MemberDto memberDto;

    public static Faq toEntity(FaqDto faqDto) {
        return Faq.builder()
                .faqId(faqDto.getFaqId())
                .faqCategory(faqDto.getFaqCategory())
                .question(faqDto.getQuestion())
                .member(MemberDto.toEntity(faqDto.getMemberDto()))
                .answer(faqDto.getAnswer())
                .faqViews(faqDto.getFaqViews())
                .build();
    }

    public static List<Faq> toEntityList(List<FaqDto> faqDtoList) {
        List<Faq> faqList = new ArrayList<>();
        faqDtoList.forEach(faqDto ->
                faqList.add(FaqDto.toEntity(faqDto))
        );
        return faqList;
    }
}
