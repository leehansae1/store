package org.example.store.follow;

import lombok.*;
import org.example.store.member.entity.Member;

import java.util.ArrayList;
import java.util.List;

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

    public static List<Follow> toEntityList(List<FollowDto> followDtoList) {
        List<Follow> follows = new ArrayList<>();
        followDtoList.forEach(followDto -> follows.add(toEntity(followDto)));
        return follows;
    }
}
