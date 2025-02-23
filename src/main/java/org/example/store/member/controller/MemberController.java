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
  private final String prefix = "member";

  @GetMapping("/signup")
  public String signup(Model model) {
    model.addAttribute("signupDto", new SignupDto());
    return prefix + "/signup";
  }

  @PostMapping("/signup")
  public String signup(@Valid @ModelAttribute SignupDto signupDto, BindingResult bindingResult) {
    log.info("Received SignupDto: {}", signupDto);
    if (bindingResult.hasErrors()) {
      return prefix + "/signup";
    }
    Member savedMember = memberService.signup(signupDto);
    if (savedMember != null) {
      return "redirect:/member/login";
    }
    return prefix + "/signup";
  }

  @PostMapping("/check-duplicate")
  @ResponseBody
  public Map<String, Boolean> checkDuplicateId(@RequestBody String userId) {
    userId = userId.replaceAll("^\"|\"$", "");
    boolean isExisted = memberRepository.existsByUserId(userId);
    return Map.of("isExisted", isExisted);
  }

  @GetMapping("/login")
  public String login(Model model) {
    model.addAttribute("loginDto", new LoginDto());
    return prefix + "/login";
  }

  @PostMapping("/login")
  public String login(@Valid @ModelAttribute LoginDto loginDto, BindingResult bindingResult, HttpServletRequest request) {
    AuthenticationException exception = (AuthenticationException) request.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    if (exception instanceof BadCredentialsException) {
      bindingResult.reject("BadCredentials", "아이디 또는 패스워드를 확인해 주세요.");
    } else if (exception instanceof UsernameNotFoundException) {
      bindingResult.reject("UserNotFound", "사용자를 찾을 수 없습니다.");
    } else if (exception != null) {
      bindingResult.reject("AuthenticationException");
    }
    if (bindingResult.hasErrors()) {
      log.info("로그인 실패: {}", bindingResult.getAllErrors());
      return prefix + "/login";
    }
    return "/index";
  }

  @GetMapping("/info")
  public String info(Authentication authentication, Model model) {
    String currentUserId = authentication.getName();
    log.info("Current User ID: {}", currentUserId);
    Member member = memberService.findByUserId(currentUserId);
    if (member == null) {
      log.warn("No member found for userId: {}", currentUserId);
      return "redirect:/?error=NoSuchUser";
    }
    log.info("Retrieved member: {}", member);
    model.addAttribute("member", member);
    return prefix + "/info";
  }

  @GetMapping("/modify")
  public String modify(Model model, @AuthenticationPrincipal UserDetails userDetails) {
    String loginUserId = userDetails.getUsername();
    ModifyDto modifyDto = memberService.getMemberById(loginUserId);
    model.addAttribute("modifyDto", modifyDto);
    return prefix + "/modify";
  }

  @PostMapping("/modify")
  public String modify(@ModelAttribute ModifyDto modifyDto, Model model) {
    Member modifiedMember = memberService.modifiedMember(modifyDto);
    if (modifiedMember != null) {
      log.info("회원 정보 수정 성공: {}", modifiedMember.getUserId());
      return "redirect:/";
    } else {
      log.info("회원 정보 수정 실패: {}", modifyDto.getUserId());
      return "redirect:/member/info";
    }
  }

  @PostMapping("/delete")
  public String delete(@RequestParam("userPw") String userPw, Authentication authentication,
                       Model model, RedirectAttributes redirectAttributes) {
    String currentUserId = authentication.getName();
    log.info("회원 삭제 요청, userId: {}", currentUserId);
    boolean isDeleted = memberService.deleteMember(currentUserId, userPw);
    if (isDeleted) {
      log.info("회원 삭제 성공, userId: {}", currentUserId);
      redirectAttributes.addFlashAttribute("message", "계정이 삭제되었습니다.");
      return "redirect:/";
    }
    log.warn("회원 삭제 실패, userId: {}", currentUserId);
    redirectAttributes.addFlashAttribute("error", "삭제 실패: 패스워드가 일치하지 않습니다.");
    return "redirect:/member/info";
  }

  @GetMapping("/admin/deleted-members")
  public String deletedMembers(Model model) {
    model.addAttribute("deletedMembers", memberService.getDeletedMembers());
    return "/admin/deleted-members";
  }

  @PostMapping("/follow/{sellerId}")
  @ResponseBody
  public Map<String, Boolean> follow(@PathVariable String sellerId, @AuthenticationPrincipal CustomUserDetails user) {
    log.info("팔로우 요청");
    boolean isSaved = memberService.follow(sellerId, user);
    return Map.of("isSaved", isSaved);
  }

  @DeleteMapping("/follow/{sellerId}")
  @ResponseBody
  public Map<String, Boolean> unFollow(@PathVariable String sellerId, @AuthenticationPrincipal CustomUserDetails user) {
    log.info("언팔 요청");
    int deleteResult = memberService.unfollow(sellerId, user);
    return Map.of("isDelete", deleteResult > 0);
  }
}
