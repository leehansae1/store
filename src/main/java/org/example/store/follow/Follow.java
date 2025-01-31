package org.example.store.follow;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.store.member.Member;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
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

    @CreatedDate
    private LocalDateTime followAt;

    @Builder
    public Follow(Member seller, Member follower, LocalDateTime followAt, int followId) {
        this.seller = seller;
        this.follower = follower;
        this.followAt = followAt;
        this.followId = followId;
    }

    public static FollowDto fromEntity(Follow follow){
        return FollowDto.builder()
                .followId(follow.getFollowId())
                .seller(follow.getSeller())
                .follower(follow.getFollower())
                .followAt(follow.getFollowAt())
                .build();
    }
}
