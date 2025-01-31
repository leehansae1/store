package org.example.store_project.faq;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.store_project.member.Member;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class Faq {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int faqId;

    private String faqCategory;

    private String question;

    @Size(min = 1, max = 2000)
    private String answer;

    private int faqViews;

    // 작성한 관리자 색출 아이디
    @ManyToOne
    @JoinColumn(name = "admin_id", referencedColumnName = "userId")
    private Member member;

    @Builder
    public Faq(int faqId, String faqCategory, int faqViews,
               String question, String answer, Member member) {
        this.member = member;
        this.faqId = faqId;
        this.faqCategory = faqCategory;
        this.question = question;
        this.answer = answer;
        this.faqViews = faqViews;
    }

    public static FaqDto fromEntity(Faq faq) {
        return FaqDto.builder()
                .faqId(faq.getFaqId())
                .faqCategory(faq.getFaqCategory())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .faqViews(faq.getFaqViews())
                .memberDto(Member.fromEntity(faq.getMember()))
                .build();
    }
}
