package org.example.store.follow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.dto.MemberDto;
import org.example.store.member.entity.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    // 팔로우 저장
    public boolean follow(Member seller, Member follower) {
        Follow follow = Follow.builder()
                .seller(seller)
                .follower(follower)
                .build();
        Follow savedfollow = followRepository.save(follow);
        return Follow.fromEntity(savedfollow) != null;
    }
    // 삭제
    @Transactional
    public int unfollow(Member seller, Member follower) {
        return followRepository.deleteBySellerAndFollower(seller, follower);
    }

    // 구독 여부 (상점 정보 조회 시 & 상품 상세 열람 시 필요)
    public boolean isFollowed(Member seller, Member follower) {
        return followRepository.existsBySellerAndFollower(seller, follower);
    }

    // 팔로워 수
    public int getFollowCount(Member seller) {
        return followRepository.countBySeller(seller);
    }

    // 밑에 두가지 메서드 >> 내 상점 조회 & 내 계정 조회 시 필요함
    // 나를 구독한 >> 팔로워 내역
    public List<MemberDto> getAllFollowers(Member member) {
        List<MemberDto> memberDtoList = new ArrayList<>();
        List<Follow> followList = followRepository.findAllBySeller(member);
        followList.forEach(follow ->
                memberDtoList.add(Member.fromEntity(follow.getFollower()))
        );
        if (memberDtoList.isEmpty()) log.info("팔로워가 없어용,,,");
        else memberDtoList.forEach(m -> log.info("memberList == {}", m));
        return memberDtoList;
    }

    // 내가 구독한 >> 팔로잉 내역
    public List<MemberDto> getAllSeller(Member member) {
        List<MemberDto> memberDtoList = new ArrayList<>();
        List<Follow> followList = followRepository.findAllByFollower(member);
        followList.forEach(follow ->
                memberDtoList.add(Member.fromEntity(follow.getFollower()))
        );

        if (memberDtoList.isEmpty()) log.info("팔로잉이 없어용,,,");
        else memberDtoList.forEach(m -> log.info("memberList == {}", m));
        return memberDtoList;
    }
}
