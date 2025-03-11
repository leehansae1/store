package org.example.store.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.dto.CustomUserDetails;
import org.example.store.member.dto.LoginDto;
import org.example.store.member.dto.MemberDto;
import org.example.store.member.entity.Member;
import org.example.store.member.repository.MemberRepository;
import org.example.store.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    private final String prefix = "member";

    // 회원가입 화면
    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("member", new MemberDto());
        return prefix + "/signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute MemberDto memberDto) {
        log.info("Received memberDto: {}", memberDto);
        return memberService.signup(memberDto) ? "redirect:/member/login" : "member/signup";
    }

    // 아이디 중복 검사
    @PostMapping("/check-duplicate")
    @ResponseBody
    public Map<String, Boolean> checkDuplicateId(@RequestBody String userId) {
        String renamedId = userId.replaceAll("^\"|\"$", "");
        boolean isExisted = memberService.isExistedId(renamedId);
        return Map.of("isExisted", isExisted);
    }

    // 로그인
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginDto", new LoginDto());
        return prefix + "/login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginDto loginDto, BindingResult bindingResult, HttpServletRequest
            request) {
        AuthenticationException exception
                = (AuthenticationException) request.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        if (exception instanceof UsernameNotFoundException) {
            bindingResult.reject("UserNotFound", "사용자를 찾을 수 없습니다.");
        } else if (exception instanceof BadCredentialsException) {
            bindingResult.reject("BadCredentials", "아이디 또는 패스워드를 확인해 주세요.");
        } else if (exception != null) bindingResult.reject("AuthenticationException");
        if (bindingResult.hasErrors()) {
            return prefix + "/login";
        }
        return "/index";
    }

    // 회원 정보 조회
    @GetMapping("/info")
    public String info(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        MemberDto member = memberService.getMemberDto(user.getLoggedMember().getUserId());
        member.setUserPw("");
        model.addAttribute("member", member);
        return prefix + "/info";
    }

    @PostMapping("/update-info")
    @ResponseBody
    public Map<String, Boolean> updateInfo(@AuthenticationPrincipal CustomUserDetails user,
                                           @RequestBody Map<String, Object> requestData) {
        String updateTarget = (String) requestData.get("updateTarget");
        int category = (int) requestData.get("category");
        boolean updated = memberService.updateMember(user, updateTarget, category);
        return Map.of("updated", updated);
    }

    // 현재 비밀번호 확인 (비밀번호 검증 후 반환)
    @PostMapping("/verify-password")
    @ResponseBody
    public Map<String, Boolean> verifyPassword(@AuthenticationPrincipal CustomUserDetails user,
                                               @RequestBody Map<String, String> password) {
        boolean isVerified = memberService.verifyPassword(user.getLoggedMember().getUserId(), password.get("password"));
        return Map.of("matched", isVerified);
    }

    // 회원 soft 탈퇴
    @PostMapping("/delete")
    @ResponseBody
    public Map<String, Boolean> delete(@AuthenticationPrincipal CustomUserDetails user) {
        log.info("회원 삭제 요청, userId: {}", user.getLoggedMember().getUserId());
        boolean isDeleted = memberService.hideMember(user.getLoggedMember().getUserId());
        return Map.of("isDelete", isDeleted);
    }

    // 팔로우 & 언팔로우 기능
    @PostMapping("/follow/{sellerId}")
    @ResponseBody
    public Map<String, Boolean> follow(@PathVariable String sellerId, @AuthenticationPrincipal CustomUserDetails
            user) {
        log.info("팔로우 요청");
        boolean isSaved = memberService.follow(sellerId, user);
        return Map.of("isSaved", isSaved);
    }

    @DeleteMapping("/follow/{sellerId}")
    @ResponseBody
    public Map<String, Boolean> unFollow(@PathVariable String sellerId, @AuthenticationPrincipal CustomUserDetails
            user) {
        log.info("언팔 요청");
        int deleteResult = memberService.unfollow(sellerId, user);
        return Map.of("isDelete", deleteResult > 0);
    }

    // 관리자 계정 접속 시 탈퇴한 회원 조회 가능
    @GetMapping("/admin/deleted-members")
    public String deletedMembers(Model model) {
        model.addAttribute("deletedMembers", memberService.getDeletedMembers());
        return "/admin/deleted-members";
    }
}
