package org.example.store.follow;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.store.member.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int followId;

    @ManyToOne
    @JoinColumn(name = "logged_id")
    private Member seller;

    @ManyToOne
    @JoinColumn(name = "follower_id")
    private Member follower;

    @Builder
    public Follow(Member seller, Member follower, int followId) {
        this.seller = seller;
        this.follower = follower;
        this.followId = followId;
    }

    public static FollowDto fromEntity(Follow follow) {
        return FollowDto.builder()
                .followId(follow.getFollowId())
                .seller(follow.getSeller())
                .follower(follow.getFollower())
                .build();
    }
}
