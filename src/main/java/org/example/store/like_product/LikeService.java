package org.example.store.like_product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.dto.CustomUserDetails;
import org.example.store.member.entity.Member;
import org.example.store.product.dto.ProductDto;
import org.example.store.product.entity.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    // 로그인 계정과 상품아이디로 저장
    public boolean saveLike(Product product, Member liker) {
        LikeProductDto likeProductDto = LikeProductDto.builder()
                .productDto(Product.fromEntity(product))
                .liker(Member.fromEntity(liker))
                .build();

        LikeProduct likeProduct
                = likeRepository.save(LikeProductDto.toEntity(likeProductDto));
        return LikeProduct.fromEntity(likeProduct) != null;
    }

    // 로그인 계정과 상품아이디로 삭제
    @Transactional
    public int deleteLike(Product product, Member liker) {
        return likeRepository.deleteByProductAndLiker(product, liker);
    }

    // 하나의 상품에 달린 좋아요 수 >> product service 에서 호출됨
    public int getLikeCount(Product product) {
        return likeRepository.countByProduct(product);
    }

    // 찜 여부 >> product service 에서 호출됨
    public boolean isLiked(CustomUserDetails liker, Product product) {
        return likeRepository.existsByLikerAndProduct(liker.getLoggedMember(), product);
    }

    // 내가 찜한 상품 출력
    public List<ProductDto> getLikeProducts(CustomUserDetails liker) {
        List<ProductDto> productDtoList = new ArrayList<>();

        List<LikeProduct> likeProductList = likeRepository.findAllByLiker(liker.getLoggedMember());
        likeProductList.forEach(likeProduct ->
                productDtoList.add(Product.fromEntity(likeProduct.getProduct()))
        );
        return productDtoList;
    }
}
