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
        return memberService.signup(memberDto) ? "redirect:member/login" : prefix + "/signup";
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
        } else if (exception != null) {
            bindingResult.reject("AuthenticationException");
        }
        if (bindingResult.hasErrors()) {
            log.info("로그인 실패: {}", bindingResult.getAllErrors());
            return prefix + "/login";
        }
        return "/index";
    }

    // 회원 정보 조회
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

    // 회원 정보 수정
    @GetMapping("/modify")
    public String modify(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        String loginUserId = user.getUsername();
        MemberDto memberDto = memberService.getMemberDto(loginUserId);
        model.addAttribute("member", memberDto);
        return prefix + "/modify";
    }

    @PostMapping("/modify")
    public String modify(@ModelAttribute MemberDto memberDto) {
        Member modifiedMember = memberService.modifiedMember(memberDto);
        if (modifiedMember != null) {
            log.info("회원 정보 수정 성공");
            return "redirect:/member/logout";
        } else {
            log.info("회원 정보 수정 실패");
            return "/member/modify";
        }
    }

    // 회원 soft 탈퇴
    @PostMapping("/delete")
    public String delete(@RequestParam("userPw") String userPw, Authentication authentication,
                         Model model, RedirectAttributes redirectAttributes) {
        String currentUserId = authentication.getName();
        log.info("회원 삭제 요청, userId: {}", currentUserId);
        boolean isDeleted = memberService.hideMember(currentUserId, userPw);
        if (isDeleted) {
            log.info("회원 삭제 성공, userId: {}", currentUserId);
            redirectAttributes.addFlashAttribute("message", "계정이 삭제되었습니다.");
            return "redirect:/";
        }
        log.warn("회원 삭제 실패, userId: {}", currentUserId);
        redirectAttributes.addFlashAttribute("error", "삭제 실패: 패스워드가 일치하지 않습니다.");
        return "redirect:/member/info";
    }

    // 관리자 계정 접속 시 탈퇴한 회원 조회 가능
    @GetMapping("/admin/deleted-members")
    public String deletedMembers(Model model) {
        model.addAttribute("deletedMembers", memberService.getDeletedMembers());
        return "/admin/deleted-members";
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
}
