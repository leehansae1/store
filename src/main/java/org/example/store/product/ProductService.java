package org.example.store.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.follow.FollowService;
import org.example.store.like_product.LikeService;
import org.example.store.member.entity.Member;
import org.example.store.member.dto.MemberDto;
import org.example.store.product.dto.ImageDto;
import org.example.store.product.dto.ProductDto;
import org.example.store.product.entity.Image;
import org.example.store.product.entity.Product;
import org.example.store.product.repository.ImageRepository;
import org.example.store.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ImageRepository imageRepository;

    private final LikeService likeService;

    private final FollowService followService;

    // product 가 필요할 때
    public Product getProduct(int productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        return optionalProduct.orElse(null);
    }

    // 검색키워드로 상품 조회 -- image 테이블은 참조할 필요 없음
    public List<ProductDto> getProductList(String searchWord) {
        List<ProductDto> productDtoList = new ArrayList<>();
        // LIKE 쿼리가 귀찮아서 ㅎㅎ,,,,
        List<Product> productList = productRepository
                .findAllByDescriptionContainingOrCategoryContainingOrTagContainingOrProductNameContaining
                        (searchWord, searchWord, searchWord, searchWord);
        productList.forEach(product ->
                productDtoList.add(Product.fromEntity(product))
        );
        return productDtoList;
    }

    // 상품 상세 화면 >> 상품 정보 (+찜여부, 찜개수), 판매자 정보(이름, 판매자 프로필사진 등 (+팔로우 여부, 팔로우 수))
    // 추후 매개변수에 custom detail 유저로 수정
    public Map<String, Object> getProductDetail(int productId, Member 로그인한계정) {
        Map<String, Object> map = new HashMap<>();

        Product product = getProduct(productId);
        ProductDto productDto = Product.fromEntity(product);

        // 상품에 대한 찜 개수
        int likeCount = likeService.getLikeCount(product);
        productDto.setLikeCount(likeCount);
        // 내가 찜했는지 여부
        boolean isLiked = likeService.isLiked
                (로그인한계정, product);
        productDto.setLikeState(isLiked);
        map.put("product", productDto);

        // 판매자 계정 dto
        MemberDto memberDto = Member.fromEntity(product.getSeller());
        // 팔로우 수
        int followCount = followService.getFollowCount(product.getSeller());
        memberDto.setFollowCount(followCount);
        // 팔로우 여부
        boolean isFollowed = followService.isFollowed(product.getSeller(), 로그인한계정);
        memberDto.setFollowState(isFollowed);
        map.put("member", memberDto);

        // 상품 이미지 테이블 조회
        // 프론트에서 값을 꺼낼 때 리스트로 꺼내면 더 간단하기 때문에 image 도메인을 리스트로 변환
        map.put("imageList", ImageDto.toList(Image.fromEntity(product.getImage())));

        return map;
    }

    // 상품 업로드 >> 이미지, 프로덕트 테이블 채우기
    public int uploadProduct(ProductDto productDto, List<MultipartFile> files, Member 내계정) {
        //= 파일 이름 바꿔준다, 파일 규격화한다, 파일 폴더에저장한다 fileList.get(i) >> 요 로직만 만들면 됨
        List<String> imageUrlList = new ArrayList<>();
        files.forEach(multipartFile -> {
            imageUrlList.add(multipartFile.getOriginalFilename());
        }); //요런 느낌

        productDto.setThumbnailUrl(imageUrlList.getFirst());
        imageUrlList.removeFirst();
        productDto.setSeller(Member.fromEntity(내계정));
        Product product = productRepository.save(ProductDto.toEntity(productDto));

        ImageDto imageDto = ImageDto.toDto(imageUrlList, product);
        if (imageDto != null) imageRepository.save(ImageDto.toEntity(imageDto));
        return Product.fromEntity(product) != null ? product.getProductId() : 0;
    }

    // 좋아요 저장 , 삭제
    public boolean like(int productId, Member 내계정) {
        Product product = getProduct(productId);
        return likeService.saveLike(product, 내계정);
    }

    public int unlike(int productId, Member 내계정) {
        Product product = getProduct(productId);
        return likeService.deleteLike(product, 내계정);
    }

    // 판매자 OR 내 물건 리스트 조회 (상점 안의 상품리스트 or 상품리스트 관리 페이지)
    public List<ProductDto> getSellerProducts(Member seller) {
        List<ProductDto> productDtos = new ArrayList<>();
        List<Product> products = productRepository.findAllBySeller(seller);
        products.forEach(product -> productDtos.add(Product.fromEntity(product)));
        return productDtos;
    }
}
