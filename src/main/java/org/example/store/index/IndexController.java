package org.example.store.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping(value = {"/index", "/"})
    public String index() {
        return "redirect:product/list";
    }
}
