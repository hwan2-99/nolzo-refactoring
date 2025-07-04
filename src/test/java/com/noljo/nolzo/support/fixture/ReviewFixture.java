package com.noljo.nolzo.support.fixture;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.review.entity.Review;
import lombok.Getter;

@Getter
public enum ReviewFixture {
    영화리뷰("영화 정말 재밌었어요!", 5, EventFixture.캣츠(), MemberFixture.회원()),
    연극리뷰("연극 구성이 훌륭했습니다.", 4, EventFixture.햄릿(), MemberFixture.회원());

    private String content;
    private int rating;
    private Event event;
    private Member member;

    ReviewFixture(String content, int rating, Event event, Member member) {
        this.content = content;
        this.rating = rating;
        this.event = event;
        this.member = member;
    }

    public static Review 영화리뷰(Event event, Member member) {
        return new Review(영화리뷰.content, 영화리뷰.rating, event, member);
    }

    public static Review 연극리뷰(Event event, Member member) {
        return new Review(연극리뷰.content, 연극리뷰.rating, event, member);
    }
}
