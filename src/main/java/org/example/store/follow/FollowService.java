package org.example.store.follow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.Member;
import org.example.store.member.MemberDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    // 팔로우 저장 / 삭제
    public boolean follow(Member seller, Member 내계정) {
        Follow follow = Follow.builder()
                .seller(seller)
                .follower(내계정)
                .build();
        Follow savedfollow = followRepository.save(follow);
        return Follow.fromEntity(savedfollow) != null;
    }
    public int unfollow(Member seller, Member 내계정) {
        return followRepository.deleteBySellerAndFollower(seller, 내계정);
    }

    // 구독 여부 (상점 정보 조회 시 & 상품 상세 열람 시 필요)
    public boolean isFollowed(Member seller, Member member) {
        return followRepository.existsBySellerAndFollower(seller, member);
    }
    // 팔로워 수
    public int getFollowCount(Member seller) {
        return followRepository.countBySeller(seller);
    }

    // 밑에 두가지 메서드 >> 팔로워 내역 + 팔로잉 내역은 내 상점 조회 & 내 계정 조회 시 필요함
    // 나를 구독한
    public List<MemberDto> getAllFollowers(Member 내계정) {
        List<MemberDto> memberDtoList = new ArrayList<>();
        List<Follow> followList = followRepository.findAllBySeller(내계정);
        followList.forEach(follow ->
                memberDtoList.add(Member.fromEntity(follow.getFollower()))
        );
        return memberDtoList;
    }
    // 내가 구독한
    public List<MemberDto> getAllSeller(Member 내계정) {
        List<MemberDto> memberDtoList = new ArrayList<>();
        List<Follow> followList = followRepository.findAllByFollower(내계정);
        followList.forEach(follow ->
                memberDtoList.add(Member.fromEntity(follow.getFollower()))
        );
        return memberDtoList;
    }
}
