package org.example.store.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.follow.FollowService;
import org.example.store.like_product.LikeService;
import org.example.store.member.Member;
import org.example.store.member.MemberDto;
import org.example.store.product.dto.ImageDto;
import org.example.store.product.dto.ProductDto;
import org.example.store.product.entity.Product;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final LikeService likeService;

    private final FollowService followService;

    // 다른 서비스에서 product 가 필요할 때
    public Product getProduct(int productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        return optionalProduct.orElse(null);

        File[] files = new File[];
        ImageDto imageDto = null;
        List<File> fileList = Arrays.stream(files).toList();
        for (int i = 0; i < fileList.size(); i++) {
            //String renameFile = 파일 이름 바꿔준다, 파일 규격화한다, 파일 폴더에저장한다 fileList.get(i)
            if (i == 0) imageDto.setImage01(renameFile);
            if (i == 1) imageDto.setImage02(renameFile);
            if (i == 2) imageDto.setImage03(renameFile);
            if (i == 3) imageDto.setImage04(renameFile);
            if (i == 4) imageDto.setImage05(renameFile);
            if (i == 5) imageDto.setImage06(renameFile);
            if (i == 6) imageDto.setImage07(renameFile);
        }
        //imageDto >> image
        //imageRepository.save(image);
    }

    // 검색키워드로 상품 조회
    public List<ProductDto> getProductList(String searchWord) {
        List<ProductDto> productDtoList = new ArrayList<>();
        List<Product> productList
                = productRepository
                .findAllByDescriptionContainingIgnoreCaseOrProductNameContainingIgnoreCase(
                        searchWord, searchWord
                );
        productList.forEach(product -> {
            // 엔티티 >> dto 변환
        });
        return productDtoList;
    }


    // 상품 상세 화면 >> 상품 정보 (+찜여부, 찜개수), 판매자 정보(이름, 판매자 프로필사진 등 (+팔로우 여부, 팔로우 수))
    // 추후 매개변수에 custom detail 유저로 수정
    public Map<String, Object> getProductDetail(int productId, Member 로그인한계정) {
        Map<String, Object> map = new HashMap<>();

        Product product;
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) product = optionalProduct.get();
        else return null;

        // product >> dto 변환
        ProductDto productDto = new ProductDto();

        // 상품에 대한 찜 개수
        int likeCount = likeService.getLikeCount(productId);
        productDto.setLikeCount(likeCount);

        // 내가 찜했는지 여부
        int isLiked
                = likeService.isLiked(로그인한계정, productId);
        productDto.likeState(isLiked);

        // 프로덕트 dto 담기
        map.put("product", productDto);

        // 판매자 계정 dto
        MemberDto memberDto = Member.fromEntity(product.getMember());

        // 판매자 팔로우 수
        int followCount = followService.getFollowerCount(product.getMember());
        memberDto.setFollowCount(followCount);

        // 팔로우 여부
        int isFollowed =
                followService.isFollowed(product.getMember(), 로그인한계정);
        memberDto.setFollowState(isFollowed);

        map.put("member", memberDto);

        return map;
    }

    // 업로드 메서드

    // 판매상태
}
