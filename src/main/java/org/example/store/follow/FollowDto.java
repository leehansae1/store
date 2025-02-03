package org.example.store.follow;

import lombok.*;
import org.example.store.member.Member;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowDto {

    private int followId;

    private Member seller;

    private Member follower;

    public static Follow toEntity(FollowDto followDto) {
        return Follow.builder()
                .followId(followDto.getFollowId())
                .seller(followDto.getSeller())
                .follower(followDto.getFollower())
                .build();

    }
}
