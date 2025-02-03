package org.example.store.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
  @GetMapping({"/","index","/home"})
  public String getMethodName() {
    return "/index/index";
  }
}
