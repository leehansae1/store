package org.example.store.like_product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.Member;
import org.example.store.member.MemberService;
import org.example.store.product.ProductService;
import org.example.store.product.dto.ProductDto;
import org.example.store.product.entity.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    private final MemberService memberService;

    private final ProductService productService;

    // 로그인 계정과 상품아이디로 좋아요저장
    public boolean save(LikeProductDto likeProductDto, int productId, String userId) {
        Product product = productService.getProduct(productId);
        likeProductDto.setProduct(Product.fromEntity(product));
        Member member = memberService.getMember(userId);
        likeProductDto.setLiker(Member.fromEntity(member));

        LikeProduct likeProduct
                = likeRepository.save(LikeProductDto.toEntity(likeProductDto));
        return LikeProduct.fromEntity(likeProduct) != null;
    }

    // 좋아요아이디(=like_product table 의 시퀀스) 로 삭제
    public int delete(int likeId) {
        return likeRepository.deleteById(likeId);
    }

    // 하나의 상품에 달린 좋아요 수 >> product service 에서 불러오기
    public int getLikeCount(int productId) {
        return likeRepository.countByProduct_ProductId(productId);
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

    // 찜 여부 >> 상품 상세화면에서 사용
    public int isLiked(Member liker, int productId) {
        LikeProduct likeProduct;
        Optional<LikeProduct> optionalLikeProduct
                = likeRepository.findByLikerAndProduct_ProductId(liker, productId);
        if (optionalLikeProduct.isPresent()) {
            likeProduct = optionalLikeProduct.get();
            return likeProduct.getId();
        }
        return 0;
    }
}
