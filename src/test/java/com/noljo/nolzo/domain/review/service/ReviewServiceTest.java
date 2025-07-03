package com.noljo.nolzo.domain.review.service;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.repository.EventRepository;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.review.dto.request.ReviewUpdateRequest;
import com.noljo.nolzo.review.dto.response.ReviewUpdateResponse;
import com.noljo.nolzo.review.entity.Review;
import com.noljo.nolzo.review.repository.ReviewRepository;
import com.noljo.nolzo.review.service.ReviewService;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.EventFixture;
import com.noljo.nolzo.support.fixture.MemberFixture;
import com.noljo.nolzo.support.fixture.ReviewFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;

@ServiceTest
public class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 리뷰를_수정할_수_있다() {
        Member member = MemberFixture.회원();
        memberRepository.save(member);

        Event event = EventFixture.캣츠();
        eventRepository.save(event);

        Review review = ReviewFixture.연극리뷰(event, member);
        reviewRepository.save(review);

        ReviewUpdateRequest request = new ReviewUpdateRequest("수정된 리뷰", 5);
        ReviewUpdateResponse response = reviewService.update(member.getId(), review.getId(), request);

        assertThat(response.content()).isEqualTo("수정된 리뷰");
        assertThat(response.rating()).isEqualTo(5);
    }
}
