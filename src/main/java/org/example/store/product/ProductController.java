package org.example.store.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.Member;
import org.example.store.product.dto.ImageDto;
import org.example.store.product.dto.ProductDto;
import org.example.store.product.entity.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
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
            log.info("found {} products to search", productDtoList.size());
            model.addAttribute("productList", productDtoList);
        }
        return prefix + "/list";
    }

    // 상품 판매글 작성페이지 및 저장
    @GetMapping("/upload")
    public String getUploadPage(Model model) {
        model.addAttribute("product", new ProductDto());
        return prefix + "/upload";
    }

    @PostMapping("/upload")
    public String uploadProduct(ProductDto productDto, ImageDto imageDto) {
        // 밸리데이션 추가 !!!!!
        Member 내계정 = null; // 작성자 아이디는 어센틱어쩌구 이용
        String productId = productService.uploadProduct(productDto, 내계정);
        // 저장이 되었다면 상세화면으로 넘어가기
        if (productId != null) return "redirect:" + prefix + "/detail/" + productId;
        else return prefix + "/upload";
    }

    // 상품 수정 페이지 >> 업로드 페이지와 같음
    @GetMapping("/modify/{productId}")
    public String getModifyPage(Model model, @PathVariable int productId) {
        ProductDto productDto = Product.fromEntity(productService.getProduct(productId));
        model.addAttribute("product", productDto);
        model.addAttribute("isSelect", productDto != null);
        return prefix + "/modify"; // 꼭 수정해라 !!
    }

    // 상품 상세 페이지
    @GetMapping("/detail/{productId}")
    public String getProduct(@PathVariable int productId, Model model, Member 로그인한계정) {
        Map<String, Object> resultMap = productService.getProductDetail(productId, 로그인한계정);
        model.addAttribute("product", resultMap.get("product"));
        log.info("found {} product", resultMap.get("product"));
        return prefix + "/detail/" + productId;
    }

    // 내 상품 관리 페이지
    @GetMapping("/manage")
    public String getManagePage(Model model, Member 내계정) {
        List<ProductDto> productDtoList = productService.getSellerProducts(내계정);
        model.addAttribute("productList", productDtoList);
        log.info("found {} products", productDtoList.size());
        return prefix + "/manage";
    }

    // 좋아요 관련 로직
    @PostMapping("/product/like/{productId}")
    public Map<String, Boolean> like(@PathVariable int productId) {
        Member 내계정 = null; // 내계정 >> @어센티 어쩌구 어노테이션으로 변경하기
        return productService.like(productId, 내계정)
                ? Map.of("success", true) : Map.of("success", false);
    }

    @DeleteMapping("product/unlike/{productId}")
    public Map<String, Boolean> unlike(@PathVariable int productId) {
        Member 내계정 = null; // 내계정 >> @어센티 어쩌구 어노테이션으로 변경하기
        return (productService.unlike(productId, 내계정) > 0)
                ? Map.of("success", true) : Map.of("success", false);
    }
}
