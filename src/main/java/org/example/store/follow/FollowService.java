package org.example.store.follow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.store.member.Member;
import org.example.store.member.MemberService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    private final MemberService memberService;

    // 팔로우 저장
    public boolean save(String sellerId, Member 내계정) {
        Member seller = memberService.getMember(sellerId);
        Follow follow = Follow.builder()
                .seller(seller)
                .follower(내계정)
                .build();
        Follow savedfollow = followRepository.save(follow);
        return Follow.fromEntity(savedfollow) != null;
    }

    // 팔로우 취소
    public int unfollow(int followId) {
        return followRepository.deleteFollowById(followId);
    }

    // 구독 여부 (상점 정보 조회 시 & 상품 상세 열람 시 필요)
    public int isFollowed(Member seller, Member member) {
        Follow follow;
        Optional<Follow> optionalFollow = followRepository.findBySellerAndFollower(seller, member);
        if (optionalFollow.isPresent()) {
            follow = optionalFollow.get();
            return follow.getFollowId();
        }
        else return 0;
    }

    // 팔로워 수
    public int getFollowerCount(Member seller) {
        return followRepository.countBySeller(seller);
    }


    // 밑에 두가지 >> 팔로워 내역 + 팔로잉 내역은 내 상점 조회 & 내 계정 조회 시 필요함
    // 나를 구독한
    public List<FollowDto> getAllFollowers(String myId) {
        List<FollowDto> followDtoList = new ArrayList<>();

        List<Follow> followList = followRepository.findAllBySeller_UserId(myId);
        followList.forEach(follow ->
                followDtoList.add(Follow.fromEntity(follow))
        );
        return followDtoList;
    }

    // 내가 구독한
    public List<FollowDto> getAllFollowed(String myId) {
        List<FollowDto> followDtoList = new ArrayList<>();

        List<Follow> followList = followRepository.findAllByFollower_UserId(myId);
        followList.forEach(follow ->
                followDtoList.add(Follow.fromEntity(follow))
        );
        return followDtoList;
    }
}
