package org.example.store.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    // main
    @GetMapping("/index")
    public String index() {
        return "redirect:/paroduct/list";
    }
    
    // upload
    @GetMapping("/product/upload")
    public String getProductUpload() {
        return "redirect:/paroduct/upload";
    }

    // detail
    @GetMapping("/product/detail")
    public String getProductDetail() {
        return "redirect:/product/detail";
    }
    
    // manage
    @GetMapping("/product/manage")
    public String getProductManage() {
        return "redirect:/product/manage";
    }

    // faq list
    @GetMapping("/faq/list")
    public String getFaqList() {
        return "redirect:/faq/list";
    }
    
    // faq write
    @GetMapping("/faq/write")
    public String getFaqWrite() {
        return "redirect:/faq/write";
    }
    
    // chat
    @GetMapping("/chat/chatroom")
    public String getChatChatroom() {
        return "redirect:/chat/chatRoom";
    }    
    
    // my product
    @GetMapping("/shop/products")
    public String getShopProducts() {
        return "redirect:/shop/products";
    }

    // my review
    @GetMapping("/shop/reviews")
    public String getShopReviews() {
        return "redirect:/shop/reviews";
    }
  
    
}
