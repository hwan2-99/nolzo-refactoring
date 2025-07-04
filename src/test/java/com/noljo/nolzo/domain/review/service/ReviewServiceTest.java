package com.noljo.nolzo.domain.review.service;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.repository.EventRepository;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.review.dto.request.ReviewUpdateRequest;
import com.noljo.nolzo.review.dto.response.ReviewUpdateResponse;
import com.noljo.nolzo.review.dto.request.ReviewCreateRequest;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
  
    @Test
    void 리뷰를_생성할_수_있다() {
        Member member = MemberFixture.회원();
        memberRepository.save(member);

        Event event = EventFixture.캣츠();
        eventRepository.save(event);

        Review review = ReviewFixture.연극리뷰(event, member);
        reviewRepository.save(review);

        assertThat(reviewRepository.findAll()).hasSize(1);
    }

    @Test
    void 리뷰를_작성할때_관람완료되지_않은_이벤트라면_예외가_발생한다() {
        Member member = MemberFixture.회원();
        memberRepository.save(member);

        Event event = EventFixture.캣츠();
        eventRepository.save(event);

        ReviewCreateRequest request = new ReviewCreateRequest("좋은 공연이었습니다!", 5, event.getId());

        assertThatThrownBy(() -> reviewService.create(member.getId(), request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("리뷰는 관람 완료된 이벤트에 대해서만 작성할 수 있습니다.");
    }
}
