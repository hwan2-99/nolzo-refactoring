package com.noljo.nolzo.review.service;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.application.port.out.EventPersistencePort;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.application.port.out.MemberPersistencePort;
import com.noljo.nolzo.reservation.application.port.out.ReservationPersistencePort;
import com.noljo.nolzo.review.dto.request.ReviewCreateRequest;
import com.noljo.nolzo.review.dto.request.ReviewUpdateRequest;
import com.noljo.nolzo.review.dto.response.EventReviewPageResponse;
import com.noljo.nolzo.review.dto.response.ReviewDetailResponse;
import com.noljo.nolzo.review.dto.response.ReviewResponse;
import com.noljo.nolzo.review.dto.response.ReviewUpdateResponse;
import com.noljo.nolzo.review.application.port.in.ReviewRatingUpdateUseCase;
import com.noljo.nolzo.review.application.port.in.ReviewUseCase;
import com.noljo.nolzo.review.entity.Review;
import com.noljo.nolzo.review.application.port.out.ReviewPersistencePort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService implements ReviewUseCase, ReviewRatingUpdateUseCase {
    private final ReviewPersistencePort reviewRepository;
    private final EventPersistencePort eventRepository;
    private final MemberPersistencePort memberRepository;
    private final ReservationPersistencePort reservationRepository;

    public ReviewResponse create(Long memberId, ReviewCreateRequest request) {
        Member member = memberRepository.getOrThrow(memberId);
        Event event = eventRepository.getOrThrow(request.eventId());

        validateEventParticipation(memberId, request.eventId());
        validateAlreadyReviewed(memberId, request.eventId());

        Review review = new Review(request.content(), request.rating(), event, member);
        return ReviewResponse.from(reviewRepository.save(review));
    }

    public ReviewUpdateResponse update(Long memberId, Long reviewId, ReviewUpdateRequest request) {
        Review review = reviewRepository.getOrThrow(reviewId);
        validateReviewOwner(review, memberId);
        review.update(request.content(), request.rating());
        return ReviewUpdateResponse.from(review);
    }

    public ReviewResponse getReview(Long reviewId) {
        Review review = reviewRepository.getOrThrow(reviewId);
        return ReviewResponse.from(review);
    }

    public List<ReviewResponse> getReviewsByMemberId(Long memberId) {
        List<Review> reviews = reviewRepository.findByMemberId(memberId);
        return reviews.stream()
                .map(ReviewResponse::from)
                .toList();
    }

    public ReviewResponse getReviewByMemberIdAndEventId(Long memberId, Long eventId) {
        Review review = reviewRepository.findByMemberIdAndEventId(memberId, eventId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 조회할 수 없습니다"));
        return ReviewResponse.from(review);
    }

    public EventReviewPageResponse getPagingReviewsByEventId(Long eventId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Review> reviewPage = reviewRepository.findPageByEventId(eventId, pageable);
        List<ReviewDetailResponse> reviewResponses = getReviewDetailResponsesFromReviewPage(reviewPage);
        Event event = eventRepository.getOrThrow(eventId);
        return EventReviewPageResponse.of(reviewPage, reviewResponses, event.getAverageRating(), page, size);
    }

    public void delete(Long memberId, Long reviewId) {
        Review review = reviewRepository.getOrThrow(reviewId);
        validateReviewOwner(review, memberId);
        reviewRepository.delete(review);
    }

    @Transactional
    public void updateEventAverageRate() {
        List<Event> events = eventRepository.findAll();
        events.forEach(event -> {
            double averageRating = reviewRepository.getAverageByEventId(event.getId());
            event.updateAverageRating(averageRating);
        });
    }

    private void validateReviewOwner(Review review, Long memberId) {
        if (!review.isOwner(memberId)) {
            throw new IllegalStateException("해당 리뷰를 수정할 권한이 없습니다.");
        }
    }

    private void validateEventParticipation(Long memberId, Long eventId) {
        boolean isUsed = reservationRepository.findTicketStatusUsedByMemberId(memberId).stream()
                .anyMatch(reservation -> reservation.getTickets().stream()
                        .anyMatch(ticket -> ticket.getSeat().getSchedule().getEvent().getId().equals(eventId)));
        if (!isUsed) {
            throw new IllegalStateException("리뷰는 관람 완료된 이벤트에 대해서만 작성할 수 있습니다.");
        }
    }

    private void validateAlreadyReviewed(Long memberId, Long eventId) {
        boolean isAlreadyReviewed = reviewRepository.findByMemberIdAndEventId(memberId, eventId).isPresent();
        if (isAlreadyReviewed) {
            throw new IllegalStateException("이미 해당 이벤트에 대한 리뷰를 작성하였습니다.");
        }
    }

    private List<ReviewDetailResponse> getReviewDetailResponsesFromReviewPage(Page<Review> reviewPage) {
        return reviewPage.getContent()
                .stream()
                .map(ReviewDetailResponse::from)
                .toList();
    }
}
