package org.example.store.like_product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.entity.Member;
import org.example.store.product.dto.ProductDto;
import org.example.store.product.entity.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    // 로그인 계정과 상품아이디로 저장
    public boolean saveLike(Product product, Member 내계정) {
        LikeProductDto likeProductDto = LikeProductDto.builder()
                .productDto(Product.fromEntity(product))
                .liker(Member.fromEntity(내계정))
                .build();

        LikeProduct likeProduct
                = likeRepository.save(LikeProductDto.toEntity(likeProductDto));
        return LikeProduct.fromEntity(likeProduct) != null;
    }

    // 로그인 계정과 상품아이디로 삭제
    public int deleteLike(Product product, Member 내계정) {
        return likeRepository.deleteByProductAndLiker(product, 내계정);
    }

    // 하나의 상품에 달린 좋아요 수 >> product service 에서 호출됨
    public int getLikeCount(Product product) {
        return likeRepository.countByProduct(product);
    }

    // 찜 여부 >> product service 에서 호출됨
    public boolean isLiked(Member liker, Product product) {
        return likeRepository.existsByLikerAndProduct(liker, product);
    }

    // 내가 찜한 상품 개수 >> 내상점 페이지에서 사용
    public int myLike(Member liker) {
        return likeRepository.countByLiker(liker);
    }

    // 내가 찜한 상품 출력
    public List<ProductDto> getLikeProducts(Member liker) {
        List<ProductDto> productDtoList = new ArrayList<>();

        List<LikeProduct> likeProductList = likeRepository.findAllByLiker(liker);
        likeProductList.forEach(likeProduct ->
                productDtoList.add(Product.fromEntity(likeProduct.getProduct()))
        );
        return productDtoList;
    }
}
