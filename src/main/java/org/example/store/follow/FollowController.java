package org.example.store.follow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.Member;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follow/{sellerId}")
    //내계정 >> @AuthenticationPrincipal CustomUserDetails customUserDetails 변경!!
    public Map<String, Boolean> follow(@PathVariable String sellerId, Member 내계정) {
        Map<String, Boolean> result = new HashMap<>();
        log.info("PostMapping");

        if (followService.save(sellerId, 내계정)) {
            result.put("is saved", true);
        } else result.put("is saved", false);
        return result;
    }

    @DeleteMapping("/follow/{followId}")
    // id 는 follow 테이블의 시퀀스
    public Map<String, Boolean> unFollow(@PathVariable int followId) {
        Map<String, Boolean> result = new HashMap<>();
        log.info("DeleteMapping");

        int deleteResult = followService.unfollow(followId);
        if (deleteResult > 0) result.put("is delete", true);
        else result.put("is delete", false);
        return result;
    }
}
