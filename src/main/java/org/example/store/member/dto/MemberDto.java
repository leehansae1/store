package org.example.store.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.store.chat.ChatDto;
import org.example.store.chatRoom.ChatRoomDto;
import org.example.store.faq.FaqDto;
import org.example.store.follow.FollowDto;
import org.example.store.like_product.LikeProductDto;
import org.example.store.member.constant.MemberStatus;
import org.example.store.member.constant.Role;
import org.example.store.member.entity.Member;
import org.example.store.memberReview.ReviewDto;
import org.example.store.payment.PaymentDto;
import org.example.store.product.dto.ProductDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class MemberDto {

    private String userId;
    private String userPw;
    private String userName;
    private String userProfile;
    private String userEmail;
    private String addr01;
    private String addr02;
    private String zipcode;
    private String tel;
    private Role role;
    private LocalDateTime regDate;
    private String introduce;
    private MemberStatus status; // 회원 상태 (ACTIVE, DELETED)
    private int followCount;
    private boolean followState;


    private List<FaqDto> faqDtoList;

    private List<ChatDto> chatDtoList;

    private List<ChatRoomDto> chatRoomDtoList;


    private List<ProductDto> productDtoList;

    private List<FollowDto> followDtoList;

    private List<LikeProductDto> likeProductDtoList;



    private List<ReviewDto> reviewDtoList;

    private List<PaymentDto> paymentDtoList;

    @Builder
    public MemberDto(String userId, String userPw, String userName, String userProfile,
                     String userEmail, String addr01, String addr02, String zipcode, Role role, String tel, LocalDateTime regDate,
                     String introduce, MemberStatus status , int followCount, boolean followState
                     , List<FaqDto> faqDtoList, List<ChatDto> chatDtoList,
                     List<ChatRoomDto> chatRoomDtoList, List<ProductDto> productDtoList,
                     List<LikeProductDto> likeProductDtoList, List<FollowDto> followDtoList,
                     List<ReviewDto> reviewDtoList, List<PaymentDto> paymentDtoList
                     ) {
        this.userId = userId;
        this.userPw = userPw;
        this.userName = userName;
        this.userProfile = userProfile;
        this.userEmail = userEmail;
        this.addr01 = addr01;
        this.addr02 = addr02;
        this.zipcode = zipcode;
        this.tel = tel;
        this.role = role;
        this.regDate = regDate;
        this.introduce = introduce;
        this.status = status != null ? status : MemberStatus.STATUS_ACTIVE;  // 기본 값 설정
        this.followCount = followCount;
        this.followState = followState;

        this.faqDtoList = faqDtoList;
        this.chatDtoList = chatDtoList;
        this.chatRoomDtoList = chatRoomDtoList;
        this.productDtoList = productDtoList;
        this.likeProductDtoList = likeProductDtoList;
        this.followDtoList = followDtoList;
        this.reviewDtoList = reviewDtoList;
        this.paymentDtoList = paymentDtoList;
    }

    public static Member toEntity(MemberDto memberDto) {

        return Member.builder()
                .userId(memberDto.getUserId())
                .userPw(memberDto.getUserPw()) // 암호화 제거, 서비스에서 처리
                .userName(memberDto.getUserName())
                .userProfile(memberDto.getUserProfile()) // 프로필 경로 그대로 사용
                .userEmail(memberDto.getUserEmail())
                .addr01(memberDto.getAddr01())
                .addr02(memberDto.getAddr02())
                .zipcode(memberDto.getZipcode())
                .tel(memberDto.getTel())
                .introduce(memberDto.getIntroduce())
                .role(memberDto.getRole())
                .regDate(memberDto.getRegDate())
                .status(memberDto.getStatus() != null ? memberDto.getStatus() : MemberStatus.STATUS_ACTIVE)
                
                .productList(ProductDto.toEntityList(memberDto.getProductDtoList()))
                .faqList(FaqDto.toEntityList(memberDto.getFaqDtoList()))
                .chatList(ChatDto.toEntityList(memberDto.getChatDtoList()))
                .chatRoomList(ChatRoomDto.toEntityList(memberDto.getChatRoomDtoList()))
                .likeProductList(LikeProductDto.toEntityList(memberDto.getLikeProductDtoList()))
                .paymentList(PaymentDto.toEntityList(memberDto.getPaymentDtoList()))
                .followList(FollowDto.toEntityList(memberDto.getFollowDtoList()))
                .reviewList(ReviewDto.toEntityList(memberDto.getReviewDtoList()))
                .build();
    }

}
