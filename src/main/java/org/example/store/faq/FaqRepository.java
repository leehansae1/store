package org.example.store.faq;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Integer> {
    @Modifying
    @Query("UPDATE Faq f SET f.faqViews = f.faqViews + 1 WHERE f.faqId = :faqId")
    int increaseViewCount(@Param("faqId") int faqId);
}
