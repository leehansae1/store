package org.example.store.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.dto.CustomUserDetails;
import org.example.store.product.dto.ProductDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
@Slf4j
public class ProductController {

    private final ProductService productService;

    private final String prefix = "/product";

    // 첫페이지 >> 상품리스트 전체조회
    @GetMapping("/list")
    public String getMainPage(Model model) {
        List<ProductDto> productDtoList = productService.getProductList("");
        if (productDtoList.isEmpty()) {
            log.info("No products found");
            model.addAttribute("productList", null);
        } else {
            log.info("found {} products", productDtoList.size());
            productDtoList.forEach(pL ->
                    log.info("product {}", pL)
            );
            model.addAttribute("productList", productDtoList);
        }
        return prefix + "/list";
    }

    // 상품리스트 검색조회
    @GetMapping("/list/{searchWord}")
    public String getSearchPage(@PathVariable String searchWord, Model model) {
        List<ProductDto> productDtoList = productService.getProductList(searchWord);
        if (productDtoList.isEmpty()) log.info("no products found, search word");
        else {
            log.info("search {} products", productDtoList.size());
            productDtoList.forEach(pL ->
                    log.info("product {}", pL)
            );
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

    // 작성 후 저장
    @PostMapping("/upload")
    public String uploadProduct(ProductDto productDto, @RequestBody List<MultipartFile> imageFiles,
                                @AuthenticationPrincipal CustomUserDetails user
                                //// 밸리데이션 추가 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    ) {
        int productId = productService.uploadProduct(productDto, imageFiles, user);
        // 저장이 되었다면 상세화면으로 넘어가기
        if (productId > 0) return "redirect:" + prefix + "/detail/" + productId;
        else return prefix + "/upload";
    }

    // 상품 수정 페이지 >> 업로드 페이지로 기존 값 싣고 보내기
    @GetMapping("/modify/{productId}")
    public String getModifyPage(RedirectAttributes redirectAttributes, @PathVariable int productId) {
        ProductDto productDto = productService.getProductDto(productId);
        log.info("modify product {}", productDto);
        redirectAttributes.addAttribute("product", productDto);
        // isSelect 가 true 라면 업로드 페이지 문구를 수정에 맞게 바꾸기
        redirectAttributes.addAttribute("isSelect", productDto != null);
        return prefix + "redirect:/upload";
    }

    // 상품 상세 페이지
    @GetMapping("/detail/{productId}")
    public String getProduct(@PathVariable int productId, Model model,
                             @AuthenticationPrincipal CustomUserDetails customUser) {
        Map<String, Object> resultMap = productService.getProductDetail(productId, customUser);
        model.addAttribute("product", resultMap.get("product"));
        model.addAttribute("member", resultMap.get("member"));
        model.addAttribute("imageList", resultMap.get("imageList"));
        List<String> imageList = (List<String>) resultMap.get("imageList");
        imageList.forEach(image -> {
            log.info("image {}", image);
        });
        log.info("found {} ", resultMap.get("product"));
        log.info("found {} ", resultMap.get("member"));
        log.info("found {} image ", ((List<String>) resultMap.get("imageList")).size());
        return prefix + "/detail/" + productId;
    }

    // 내 상품 관리 페이지
    @GetMapping("/manage")
    public String getManagePage(Model model,
                                @AuthenticationPrincipal CustomUserDetails customUser) {
        List<ProductDto> productDtoList = productService.getMyProducts(customUser);
        model.addAttribute("productList", productDtoList);
        return prefix + "/manage";
    }

    // 좋아요 관련 로직
    @PostMapping("/like/{productId}")
    @ResponseBody
    public Map<String, Boolean> like(@PathVariable int productId,
                                     @AuthenticationPrincipal CustomUserDetails user) {
        return productService.like(productId, user)
                ? Map.of("like", true) : Map.of("like", false);
    }

    @DeleteMapping("/unlike/{productId}")
    @ResponseBody
    public Map<String, Boolean> unlike(@PathVariable int productId,
                                       @AuthenticationPrincipal CustomUserDetails user) {
        return (productService.unlike(productId, user) > 0)
                ? Map.of("unlike", true) : Map.of("unlike", false);
    }
}