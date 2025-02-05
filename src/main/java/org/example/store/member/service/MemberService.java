package org.example.store.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.follow.FollowService;
import org.example.store.member.constant.MemberStatus;
import org.example.store.member.constant.Role;
import org.example.store.member.dto.CustomUserDetails;
import org.example.store.member.dto.MemberDto;
import org.example.store.member.dto.ModifyDto;
import org.example.store.member.dto.SignupDto;
import org.example.store.member.entity.Member;
import org.example.store.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService implements IMemberService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberRepository memberRepository;
    private final FollowService followService;


    @Value("${file.path}")
    private String filePath; // yml에서 경로를 읽어옴

    // signup
    @Override
    public Member signup(SignupDto signupDto) {
        // 1. 프로필 이미지 파일 업로드 처리
        String userProfilePath = null;
        if (signupDto.getUserProfile() != null && !signupDto.getUserProfile().isEmpty()) {
            try {
                userProfilePath = handlerFileUpload(signupDto.getUserProfile());
            } catch (IOException e) {
                log.error("file upload fail === {}", e.getMessage());
            }
        }

        // 2. 패스워드 암호화 처리
        String encodePassword = bCryptPasswordEncoder.encode(signupDto.getUserPw());

        // 3. DTO를 엔티티로 변환 (암호화된 패스워드 적용)
        MemberDto memberDto = MemberDto.builder()
                .userId(signupDto.getUserId())
                .userPw(encodePassword) // 암호화된 패스워드 전달
                .userEmail(signupDto.getUserEmail())
                .userName(signupDto.getUserName())
                .userProfile(userProfilePath)
                .tel(signupDto.getTel())
                .addr01(signupDto.getAddr01())
                .addr02(signupDto.getAddr02())
                .zipcode(signupDto.getZipcode())
                .introduce(signupDto.getIntroduce())
                .role(Role.ROLE_USER)
                .status(MemberStatus.STATUS_ACTIVE)
                .regDate(LocalDateTime.now())
                .build();

        // 4. 엔티티 저장
        Member savedMember = MemberDto.toEntity(memberDto);
        return memberRepository.save(savedMember);
    }

    private String handlerFileUpload(MultipartFile multipartFile) throws IOException {
        // 파일이 비어있으면 null
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }

        // 원본 파일명에서 확장자 추출
        String originalFilename = multipartFile.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // UUID로 새 파일명 생성
        String uuid = UUID.randomUUID().toString();
        String newFileName = uuid + extension;

        // 경로 연결 (C:\업로드\ + uuid.jpg 이런식으로 저장)
        String fullPath = filePath + File.separator + newFileName;

        // 실제 파일을 해당 경로에 저장
        File file = new File(fullPath);
        multipartFile.transferTo(file);

        return newFileName; // 전체 경로가 아닌 파일명만 저장
    }

    // modify
    @Transactional
    public Member modifiedMember(ModifyDto modifyDto) {
        // userId로 회원을 찾기위해 memberRepository에서 조회
        log.info("modifyDto.getUserId() === {}", modifyDto.getUserId());
        Optional<Member> optionalMember = memberRepository.findByUserId(modifyDto.getUserId());

        if (optionalMember.isPresent()) {
            // 존재하는 경우 회원을 가져옴
            Member member = optionalMember.get();
            log.info("found");

            // 1. 비밀번호가 변경됐을 경우 처리
            if (modifyDto.getUserPw() != null && !modifyDto.getUserPw().isEmpty()) {
                String encodedPassword = bCryptPasswordEncoder.encode(modifyDto.getUserPw());
                member.setUserPw(encodedPassword);
            }

            // 2. 이미지가 새로 넘어올 경우 업로드 후 회원 정보를 업데이트
            if (modifyDto.getUserProfile() != null && !modifyDto.getUserProfile().isEmpty()) {
                try {
                    String newProfilePath = handlerFileUpload(modifyDto.getUserProfile());

                    // DB에 새 경로 저장
                    member.setUserProfile(newProfilePath);
                } catch (IOException e) {
                    log.error("file upload fail === {}", e.getMessage());
                }
            }
            // 3. 나머지 정보 업데이트
            member.updateInfo(modifyDto);

            // 업데이트 정보를 DB에 저장하고 저장된 Member 객체를 반환
            return memberRepository.save(member);
        }
        log.info("not found");
        return null; // userId 회원이 없으면 null 반환
    }

    // delete
    @Transactional
    public boolean deleteMember(String userId, String userPw) {
        Optional<Member> optionalMember = memberRepository.findByUserId(userId); // 1. userId로 회원 검색
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();

            // 패스워드 검증
            if (!bCryptPasswordEncoder.matches(userPw, member.getUserPw())) {
                return false;
            }

            member.deleteMember(); // 2. 패스워드 일치 시 상태를 DELETED로 변경
            memberRepository.save(member); // 3. 변경사항 저장
            return true; // 4. 삭제 성공
        }
        return false; // 삭제 실패
    }


    // 삭제된 회원 목록 (관리자용)
    public List<Member> getDeletedMembers() {
        return memberRepository.findByStatus(MemberStatus.STATUS_DELETED);
    }

    @Override
    public Member findByUserId(String userId) { // Member entity를 그대로 반환
        return memberRepository.findByUserId(userId).orElse(null);
    }

    @Override
    public ModifyDto getMemberById(String loginUserId) { // modifyDto로 변환하여 반환
        // userId를 기반으로 회원 정보 조회
        Optional<Member> optionalMember = memberRepository.findByUserId(loginUserId);

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();

            // Member 엔티티를 ModifyDto로 변환하여 반환
            return ModifyDto.builder()
                    .userId(member.getUserId())
                    .userName(member.getUserName())
                    .userEmail(member.getUserEmail())
                    .addr01(member.getAddr01())
                    .addr02(member.getAddr02())
                    .zipcode(member.getZipcode())
                    .tel(member.getTel())
                    .introduce(member.getIntroduce())
                    .build();
        }

        // 해당 유저가 없을 경우 예외 처리
        throw new RuntimeException("해당 ID의 회원 정보를 찾을 수 없습니다: " + loginUserId);
    }

    public Member getMember(String userId) {
        Optional<Member> optionalMember = memberRepository.findByUserId(userId);
        return optionalMember.orElse(null);
    }

    // 팔로우 & 언팔
    public boolean follow(String sellerId, CustomUserDetails user) {
        Member seller = getMember(sellerId);
        return followService.follow(seller, user.getLoggedMember());
    }

    public int unfollow(String sellerId, CustomUserDetails user) {
        Member seller = getMember(sellerId);
        return followService.unfollow(seller, user.getLoggedMember());
    }

}
  

