package org.example.store.member.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.dto.CustomUserDetails;
import org.example.store.member.dto.LoginDto;
import org.example.store.member.dto.ModifyDto;
import org.example.store.member.dto.SignupDto;
import org.example.store.member.entity.Member;
import org.example.store.member.repository.MemberRepository;
import org.example.store.member.service.MemberService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;


@Controller
@RequestMapping("/member")
@Slf4j
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;
  private final MemberRepository memberRepository;

  private String prefix = "/member";

  // 회원가입 화면
  @GetMapping("/signup")
  public String signup(Model model) {
    model.addAttribute("signupDto",new SignupDto());
    return prefix+"/signup";
  }

  // 회원가입 처리
  @PostMapping("/signup")
  public String signup(@Valid @ModelAttribute SignupDto signupDto, BindingResult bindingResult) {
    log.info("signupDto======={}",signupDto);

    if (bindingResult.hasErrors()) {
      return prefix + "/signup";
    }

    Member savedMember = memberService.signup(signupDto);

    if (savedMember != null) {
      return "redirect:/member/login";
    }
    return prefix + "/signup";
  }

  // 아이디 중복 확인
  @PostMapping("/check-duplicate")
  @ResponseBody
  public boolean checkDuplicateId(@RequestBody Map<String, String> request) {     
    String userId = request.get("userId");
    return memberRepository.existsByUserId(userId);
  }
  
  
  // 로그인 화면
  @GetMapping("/login")
  public String login(Model model) {
    model.addAttribute("loginDto",new LoginDto());
    return prefix+"/login";
  }

  // 로그인 처리
  @PostMapping("/login")
  public String login(@Valid @ModelAttribute LoginDto loginDto, BindingResult bindingResult, HttpServletRequest request) {
    AuthenticationException exception = (AuthenticationException) request.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    if (exception instanceof BadCredentialsException) {
      bindingResult.reject("BadCredentials","아이디 또는 패스워드를 확인해 주세요.");
    } else if (exception instanceof UsernameNotFoundException) {
      bindingResult.reject("UserNotFound","사용자를 찾을 수 없습니다.");
    } else if (exception != null) {
      bindingResult.reject("AuthenticationException");
    }

    if (bindingResult.hasErrors()) {
      log.info("로그인 실패 === {}",bindingResult.getAllErrors());
      return prefix + "/login";
    }
    return "/index";
  }

  // 정보 화면
  @GetMapping("/info")
  public String info(Authentication authentication, Model model) {
    // 로그인한 사용자의 아이디를 가져온다.
    String currentUserId = authentication.getName();
    log.info("currentUserId === {}",currentUserId); // 로그인된 사용자 ID 확인

    // DB 에서 회원 조회
    Member member = memberService.findByUserId(currentUserId);
    if (member == null) {
      log.warn("No member found for userId === {}", currentUserId);
      return "redirect:/?error=NoSuchUser";
    }

    log.info("Retrieved member === {}", member);

    // model에 담아서 info.html로
    model.addAttribute("member", member);
    return prefix + "/info";
  }

  // 수정 화면
  @GetMapping("/modify")
  public String modify(Model model, @AuthenticationPrincipal UserDetails userDetails) {

    String loginUserId = userDetails.getUsername(); // 현재 로그인된 사용자 ID 가져오기
    ModifyDto modifyDto = memberService.getMemberById(loginUserId); // DB에서 사용자 정보 가져오기

    model.addAttribute("modifyDto", modifyDto);
    return prefix + "/modify";
  }

  // 수정 처리
  @PostMapping("/modify")
  public String modify(@ModelAttribute ModifyDto modifyDto, Model model) {
      
    // service 호출하여 수정된 회원 정보 처리
   Member modifiedMember = memberService.modifiedMember(modifyDto);

   if (modifiedMember != null) { // 수정된 정보가 존재하면 홈으로,
    log.info("modify success ====== {}",modifiedMember.getUserId());
    return "redirect:/";
   } else {
    log.info("modify fail", modifyDto.getUserId());
    return "redirect:/member/info"; // 실패 시 다시 정보 페이지로,
   }
}  

  // 삭제 처리
  @PostMapping("/delete")
  public String delete(@RequestParam("userPw") String userPw,
                       Authentication authentication,
                       Model model,
                       RedirectAttributes redirectAttributes) {
    // 로그인된 사용자 아이디 가져오기
    String currentUserId = authentication.getName();
    log.info("Deleting account for userId === {}", currentUserId);
    
    // 회원 삭제 처리
    boolean isDeleted = memberService.deleteMember(currentUserId, userPw);

    if (isDeleted) {
      log.info("Account deleted for userId === {}", currentUserId);
      redirectAttributes.addFlashAttribute("message", "계정이 삭제되었습니다.");
      return "redirect:/"; // 삭제 후 메인 페이지
    }

    log.warn("Failed to delete account for userId === {}", currentUserId);
    redirectAttributes.addFlashAttribute("error","삭제 실패 : 패스워드가 일치하지 않습니다.");
    return "redirect:/member/info";
  }

  // 관리자 전용 : 삭제된 회원 목록 (임시)
  @GetMapping("/admin/deleted-members")
  public String deletedMembers(Model model) {
    model.addAttribute("deletedMembers", memberService.getDeletedMembers());
    return "/admin/deleted-members"; // 관리자 전용 화면 
  }

  // 구독 & 취소 처리
  @PostMapping("/follow/{sellerId}")
  @ResponseBody
  public Map<String, Boolean> follow(@PathVariable String sellerId, @AuthenticationPrincipal CustomUserDetails user) {
    log.info("PostMapping");
    return memberService.follow(sellerId, user)
            ? Map.of("isSaved", true) : Map.of("isSaved", false);
  }
  @DeleteMapping("/follow/{sellerId}")
  @ResponseBody
  public Map<String, Boolean> unFollow(@PathVariable String sellerId, @AuthenticationPrincipal CustomUserDetails user) {
    log.info("DeleteMapping");
    int deleteResult = memberService.unfollow(sellerId, user);
    return deleteResult > 0
            ? Map.of("isDelete", true) : Map.of("isDelete", false);
  }
}
