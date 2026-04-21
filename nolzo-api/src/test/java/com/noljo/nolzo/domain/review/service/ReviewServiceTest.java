package com.noljo.nolzo.domain.review.service;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.application.port.out.EventPersistencePort;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.application.port.out.MemberPersistencePort;
import com.noljo.nolzo.review.dto.request.ReviewUpdateRequest;
import com.noljo.nolzo.review.dto.response.ReviewResponse;
import com.noljo.nolzo.review.dto.response.ReviewUpdateResponse;
import com.noljo.nolzo.review.dto.request.ReviewCreateRequest;
import com.noljo.nolzo.review.entity.Review;
import com.noljo.nolzo.review.application.port.out.ReviewPersistencePort;
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
    private ReviewPersistencePort reviewPersistencePort;
    @Autowired
    private EventPersistencePort eventPersistencePort;
    @Autowired
    private MemberPersistencePort memberPersistencePort;
  
    @Test
    void 리뷰를_생성할_수_있다() {
        Member member = MemberFixture.회원();
        memberPersistencePort.save(member);

        Event event = EventFixture.캣츠();
        eventPersistencePort.save(event);

        Review review = ReviewFixture.연극리뷰(event, member);
        reviewPersistencePort.save(review);

        assertThat(reviewPersistencePort.findAll()).hasSize(1);
    }

    @Test
    void 리뷰_작성_클릭_시_관람_완료되지_않은_이벤트라면_예외가_발생한다() {
        Member member = MemberFixture.회원();
        memberPersistencePort.save(member);

        Event event = EventFixture.캣츠();
        eventPersistencePort.save(event);

        ReviewCreateRequest request = new ReviewCreateRequest("좋은 공연이었습니다!", 5, event.getId());

        assertThatThrownBy(() -> reviewService.create(member.getId(), request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("리뷰는 관람 완료된 이벤트에 대해서만 작성할 수 있습니다.");
    }

    @Test
    void 리뷰를_수정할_수_있다() {
        Member member = MemberFixture.회원();
        memberPersistencePort.save(member);

        Event event = EventFixture.캣츠();
        eventPersistencePort.save(event);

        Review review = ReviewFixture.연극리뷰(event, member);
        reviewPersistencePort.save(review);

        ReviewUpdateRequest request = new ReviewUpdateRequest("수정된 리뷰", 5);
        ReviewUpdateResponse response = reviewService.update(member.getId(), review.getId(), request);

        assertThat(response.content()).isEqualTo("수정된 리뷰");
        assertThat(response.rating()).isEqualTo(5);
    }

    @Test
    void 이벤트_ID와_회원_ID로_관련_리뷰를_조회할_수_있다() {
        Member member = MemberFixture.회원();
        memberPersistencePort.save(member);

        Event event = EventFixture.캣츠();
        eventPersistencePort.save(event);

        Review review = ReviewFixture.연극리뷰(event, member);
        reviewPersistencePort.save(review);

        ReviewResponse result = reviewService.getReviewByMemberIdAndEventId(member.getId(), event.getId());
        assertThat(result.content()).isEqualTo(review.getContent());
        assertThat(result.rating()).isEqualTo(review.getRating());
    }
  
    @Test
    void 리뷰를_삭제할_수_있다() {
        Member member = MemberFixture.회원();
        memberPersistencePort.save(member);

        Event event = EventFixture.캣츠();
        eventPersistencePort.save(event);

        Review review = ReviewFixture.연극리뷰(event, member);
        reviewPersistencePort.save(review);

        reviewService.delete(member.getId(), review.getId());
        assertThat(reviewPersistencePort.findById(review.getId())).isEmpty();
    }
}
