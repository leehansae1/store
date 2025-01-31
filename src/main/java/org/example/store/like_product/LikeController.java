package org.example.store.like_product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    // userId 나중에 @어센티 어쩌구 어노테이션으로 변경하기
    @PostMapping("/like/save/{productId}/{userId}")
    public Map<String, Boolean> save(@PathVariable int productId, @PathVariable String userId ,
                                     LikeProductDto likeProductDto) {
        if (likeService.save(likeProductDto,productId,userId)) return Map.of("success", true);
        else return Map.of("success", false);
    }

    @DeleteMapping("like/delete/{likeId}")
    public Map<String, Boolean> delete(@PathVariable int likeId) {
        if (likeService.delete(likeId) > 0) return Map.of("success", true);
        else return Map.of("success", false);
    }

    // 상품 아이디로 좋아요 수 조회 >> 서비스에서만

    // 내 계정으로 조회 >> 내가 찜한 상품리스트 >> 서비스에서만

    // 상품 아이디와 내 계정으로 조회 >> 내가 찜한 상품이면 likeProduct id 반환 아니라면 0 반환 >> 서비스에서만
}