package org.example.store.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.follow.FollowService;
import org.example.store.member.constant.MemberStatus;
import org.example.store.member.constant.Role;
import org.example.store.member.dto.CustomUserDetails;
import org.example.store.member.dto.MemberDto;
import org.example.store.member.entity.Member;
import org.example.store.member.repository.MemberRepository;
import org.example.store.product.ProductService;
import org.example.store.product.dto.ProductDto;
import org.example.store.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberRepository memberRepository;
    private final FollowService followService;
    private final ProductService productService;

    @Value("${file.path}")
    private String folderPath; // 파일 저장 경로

    public Member getMember(String userId) {
        return memberRepository.findByUserId(userId).orElse(null);
    }

    public MemberDto getMemberDto(String userId) {
        Optional<Member> optionalMember = memberRepository.findByUserId(userId);
        if (optionalMember.isPresent()) {
            return Member.fromEntity(optionalMember.get());
        } else return MemberDto.builder().userId("조회불가").build();
    }

    public Member getMemberByRandomId(int randomId) {
        Optional<Member> optionalMember = memberRepository.findByRandomId(randomId);
        return optionalMember.orElseGet(() -> Member.builder().userId("조회불가").build());
    }

    public boolean signup(MemberDto memberDto) {
        // 프로필 이미지 파일 업로드 처리
        if (memberDto.getProfile() != null && !memberDto.getProfile().isEmpty()) {
            String userProfilePath = FileUtil.saveAndRenameFile(memberDto.getProfile(), folderPath, 0);
            memberDto.setUserProfile(userProfilePath);
        }
        // 패스워드 암호화
        String encodedPassword = bCryptPasswordEncoder.encode(memberDto.getUserPw());
        memberDto.setUserPw(encodedPassword);
        // enum 컬럼들 채우기
        memberDto.setRole(
                memberDto.getRoleStr().equals("ROLE_USER") ? Role.ROLE_USER : Role.ROLE_ADMIN
        );
        memberDto.setStatus(MemberStatus.STATUS_ACTIVE);
        // userID 외 사용자를 조회할 고유한 값 만들어주기
        String randomId = UUID.randomUUID().toString().replaceAll("[^0-9]", "");
        memberDto.setRandomId(Integer.parseInt(randomId.substring(0, 8)));

        Member member = memberRepository.save(MemberDto.toEntity(memberDto));
        return Member.fromEntity(member) != null;
    }

    public List<Member> getDeletedMembers() {
        return memberRepository.findByStatus(MemberStatus.STATUS_DELETED);
    }

    public boolean follow(String sellerId, CustomUserDetails user) {
        Member seller = getMemberByRandomId(Integer.parseInt(sellerId));
        return followService.follow(seller, user.getLoggedMember());
    }

    public int unfollow(String sellerId, CustomUserDetails user) {
        Member seller = getMemberByRandomId(Integer.parseInt(sellerId));
        return followService.unfollow(seller, user.getLoggedMember());
    }

    public boolean isExistedId(String userId) {
        return memberRepository.existsByUserId(userId);
    }

    public boolean updateImage(CustomUserDetails user, MultipartFile file) {
        MemberDto memberDto = getMemberDto(user.getLoggedMember().getUserId());
        FileUtil.deleteFile(null, user.getLoggedMember().getUserProfile());
        String fileName = FileUtil.saveAndRenameFile(file, folderPath, 0);
        memberDto.setUserProfile(fileName);
        Member updatedUser = memberRepository.save(MemberDto.toEntity(memberDto));
        // 강제로 SecurityContext 업데이트
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(updatedUser),
                null,
                user.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return updatedUser.getUserProfile().equals(fileName);
    }

    public boolean updateMember(CustomUserDetails user, String updateTarget, int category) {
        MemberDto member = Member.fromEntity(user.getLoggedMember());
        switch (category) {
            case 1:
                member.setUserPw(bCryptPasswordEncoder.encode(updateTarget));
                break;
            case 2:
                member.setUserName(updateTarget);
                break;
            case 3:
                member.setAddress(updateTarget);
                break;
            case 4:
                member.setTel(updateTarget);
                break;
            case 5:
                member.setIntroduce(updateTarget);
                break;
        }
        Member updatedUser = memberRepository.save(MemberDto.toEntity(member));
        // 강제로 SecurityContext 업데이트
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(updatedUser),
                null,
                user.getAuthorities()
        );
        if (category == 1) SecurityContextHolder.clearContext();
        else SecurityContextHolder.getContext().setAuthentication(authentication);
        return updatedUser.getUserId().equals(member.getUserId());
    }

    public Boolean verifyPassword(String userId, String userPw) {
        Member member = getMember(userId);
        return bCryptPasswordEncoder.matches(userPw, member.getUserPw());
    }

    @Transactional
    public boolean hideMember(String userId) {
        MemberDto memberDto = getMemberDto(userId);
        memberDto.setStatus(MemberStatus.STATUS_DELETED);
        Member member = memberRepository.save(MemberDto.toEntity(memberDto));
        List<ProductDto> productList = productService.getSellerProducts(member);
        productList.forEach(productDto -> {
            productService.toggleDisplay(productDto.getProductId(), false);
        });
        return member.getStatus() == MemberStatus.STATUS_DELETED;
    }
}
