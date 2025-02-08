package org.example.store.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.follow.FollowService;
import org.example.store.like_product.LikeService;
import org.example.store.member.dto.CustomUserDetails;
import org.example.store.member.entity.Member;
import org.example.store.member.dto.MemberDto;
import org.example.store.payment.PaymentRepository;
import org.example.store.product.dto.ImageDto;
import org.example.store.product.dto.ProductDto;
import org.example.store.product.entity.Image;
import org.example.store.product.entity.Product;
import org.example.store.product.repository.ImageRepository;
import org.example.store.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    @Value("${file.path}")
    private String folderPath;

    private final ProductRepository productRepository;

    private final ImageRepository imageRepository;

    private final LikeService likeService;

    private final FollowService followService;

    private final PaymentRepository paymentRepository;

    //product 가 필요할 때
    public Product getProduct(int productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        return optionalProduct.orElse(null);
    }

    // 검색키워드로 상품 조회 -- image 테이블은 참조할 필요 없음
    public List<ProductDto> getProductList(String searchWord) {
        // LIKE 쿼리 대체
        List<Product> productList = productRepository
                .findAllByDescriptionContainingOrCategoryContainingOrTagContainingOrProductNameContaining
                        (searchWord, searchWord, searchWord, searchWord);
        return Product.fromEntityList(productList);
    }

    // 상품 상세 화면 >> 상품 정보 (+찜여부, 찜개수), 판매자 정보(이름, 판매자 프로필사진 등 (+팔로우 여부, 팔로우 수))
    public Map<String, Object> getProductDetail(int productId, CustomUserDetails user) {
        Map<String, Object> map = new HashMap<>();

        Product product = getProduct(productId);
        ProductDto productDto = Product.fromEntity(product);

        // 상품에 대한 찜 개수
        int likeCount = likeService.getLikeCount(product);
        productDto.setLikeCount(likeCount);
        // 내가 찜했는지 여부
        boolean isLiked = likeService.isLiked(user, product);
        productDto.setLikeState(isLiked);
        map.put("product", productDto);

        // 판매자 계정 dto
        MemberDto memberDto = Member.fromEntity(product.getSeller());
        // 팔로우 수
        int followCount = followService.getFollowCount(product.getSeller());
        memberDto.setFollowCount(followCount);
        // 팔로우 여부
        boolean isFollowed = followService.isFollowed(product.getSeller(), user.getLoggedMember());
        memberDto.setFollowState(isFollowed);
        map.put("member", memberDto);

        // 상품 이미지 테이블 조회
        // 프론트에서 값을 꺼낼 때 리스트로 꺼내면 더 간단하기 때문에 image 도메인을 리스트로 변환
        map.put("imageList", ImageDto.fromDto(Image.fromEntity(product.getImage())));

        return map;
    }

    // 상품 업로드 >> 이미지, 프로덕트 테이블 채우기
    @Transactional
    public int uploadProduct(ProductDto productDto, List<MultipartFile> files,
                             CustomUserDetails user) {

        // 수정 시 기존 파일 삭제
        if (productDto.getProductId() != 0) {
            Product product = getProduct(productDto.getProductId());
            FileUtil.deleteFile(
                    product.getImage(), product.getThumbnailUrl()
            );
        }
        productDto.setSeller(Member.fromEntity(user.getLoggedMember())); //회원 저장

        List<String> imageUrlList = new ArrayList<>();
        files.forEach(multipartFile -> { //파일 저장 및 이름 바꿔서 변환
            String renamedFile = FileUtil.saveAndRenameFile(multipartFile, folderPath);
            imageUrlList.add(renamedFile);
        });

        productDto.setThumbnailUrl(imageUrlList.getFirst());
        log.info("썸네일 저장 후 productDto == {}", productDto);
        imageUrlList.removeFirst();

        ImageDto imageDto;
        if (imageUrlList.isEmpty()) { //대표사진만 업로드 한 경우
            imageDto = ImageDto.builder().productDto(productDto).build();
        } else imageDto = ImageDto.toDto(imageUrlList);

        productDto.setImageDto(imageDto);

        Product product = productRepository.save(ProductDto.toEntity(productDto));

        imageDto.setProductDto(Product.fromEntity(product));
        log.info("product 저장 후 imageDto == {}", imageDto);
        imageRepository.save(ImageDto.toEntity(imageDto));

        return Product.fromEntity(product) != null ? product.getProductId() : 0; //널이 아니라면 id를 뱉어낸다
    }

    public ProductDto getProductDto(int productId) {
        Product product = getProduct(productId);
        ProductDto productDto = Product.fromEntity(product);
        productDto.setImageDto(Image.fromEntity(product.getImage()));
        return productDto;
    }

    // 좋아요 저장 , 삭제
    public boolean like(int productId, CustomUserDetails user) {
        Product product = getProduct(productId);
        return likeService.saveLike(product, user.getLoggedMember());
    }

    public int unlike(int productId, CustomUserDetails user) {
        Product product = getProduct(productId);
        return likeService.deleteLike(product, user.getLoggedMember());
    }

    // 내 물건 리스트 관리를 위한 조회 (상품리스트 관리 페이지)
    public List<ProductDto> getMyProducts(CustomUserDetails user) {
        List<Product> products = productRepository.findAllBySeller(user.getLoggedMember());
        List<ProductDto> productDtoList = Product.fromEntityList(products);
        productDtoList.forEach(productDto -> {
            if (paymentRepository.findByProductAndSuccess(ProductDto.toEntity(productDto),1).isEmpty()){
                productDto.setSellStatus(true);
            }
        });
        return productDtoList;
    }

    // 판매자 물건 리스트 조회 (타인의 내 상점 안의 상품리스트)
    public List<ProductDto> getSellerProducts(MemberDto memberDto) {
        List<Product> products = productRepository.findAllBySeller(MemberDto.toEntity(memberDto));
        return Product.fromEntityList(products);
    }
}
