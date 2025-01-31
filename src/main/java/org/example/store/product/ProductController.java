package org.example.store.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.like_product.LikeService;
import org.example.store.member.Member;
import org.example.store.product.dto.ImageDto;
import org.example.store.product.dto.ProductDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    private final LikeService likeService;

    private final String prefix = "/product";

    // 첫페이지
    @GetMapping("/list")
    public String getMainPage(Model model) {
        List<ProductDto> productDtoList = productService.getProductList("");
        if (productDtoList.isEmpty()) log.info("No products found");
        else {
            log.info("found {} products", productDtoList.size());
            model.addAttribute("productList", productDtoList);
        }
        return prefix + "/list";
    }

    // 검색해서 넘김
    @GetMapping("/list/{searchWord}")
    public String getSearchPage(@PathVariable String searchWord, Model model) {
        List<ProductDto> productDtoList = productService.getProductList(searchWord);
        if (productDtoList.isEmpty()) log.info("no products found, search word");
        else {
            log.info("found {} products to search", productDtoList.size());
            model.addAttribute("productList", productDtoList);
        }
        return prefix + "/list";
    }

    // 상품 판매글 작성페이지
    @GetMapping("/upload")
    public String getUploadPage(Model model) {
        model.addAttribute("product", new ProductDto());
        return prefix + "/upload";
    }

    // 상품 판매글 저장
    @PostMapping("/upload/{작성자아이디}")
    // 작성자 아이디 >> 멤버를 찾아서 dto 에 넣어주기
    // 작성자 아이디는 어센틱어쩌구 이용
    public String uploadProduct(ProductDto productDto, ImageDto imageDto, @PathVariable String 작성자아이디) {
        // 밸리데이션 추가 !!
        String productId = productService.uploadProduct(productDto, 작성자아이디);
        // 저장이 되었다면 상세화면으로 넘어가기
        if (productId != null) return "redirect:" + prefix + "/detail/" + productId;
        else return prefix + "/upload";
    }

    // 상품 상세 페이지
    @GetMapping("/detail/{productId}")
    public String getProduct(@PathVariable int productId, Model model, Member 로그인한계정) {
        Map<String,Object> resultMap = productService.getProductDetail(productId, 로그인한계정);
        model.addAttribute("product", resultMap.get("product"));
        return prefix + "/detail/" + productId;
    }

    // 사용자 한명이 올린 상품 리스트를 조회

    // 상품 관리 페이지
    // 1) 상품 상세와 같으나 이미지 테이블은 참조할 필요 없음 >> 대표사진만 있으면 됨
    // 2) payment 테이블에 상품이 있으면 productDto 속성 중 판매상태를 나타내는 속성으로 구분 짓기 >> paymentService 메서드 이용
    // 3)

    // 상품 수정 페이지 >> 업로드 페이지와 같음

    // 내가 찜한 상품 조회 >> likeService.getLikeProducts() 이용하세요
}
