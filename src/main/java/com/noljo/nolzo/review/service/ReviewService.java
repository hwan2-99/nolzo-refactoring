package com.noljo.nolzo.review.service;

import com.noljo.nolzo.review.dto.request.ReviewUpdateRequest;
import com.noljo.nolzo.review.dto.response.EventReviewDetailsResponse;
import com.noljo.nolzo.review.dto.response.ReviewDetailResponse;
import com.noljo.nolzo.review.dto.response.ReviewUpdateResponse;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.repository.EventRepository;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.reservation.repository.ReservationRepository;
import com.noljo.nolzo.review.dto.request.ReviewCreateRequest;
import com.noljo.nolzo.review.dto.response.ReviewResponse;
import com.noljo.nolzo.review.entity.Review;
import com.noljo.nolzo.review.repository.ReviewRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;

    public ReviewResponse create(Long memberId, ReviewCreateRequest request){
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

    public EventReviewDetailsResponse getReviewsByEventId(Long eventId) {
        List<ReviewDetailResponse> reviews  = findDetailReviewsByEvent(eventId);
        double averageRating = getAverageRating(reviews);
        return new EventReviewDetailsResponse(averageRating, reviews.size(), reviews);
    }
  
    public void delete(Long memberId, Long reviewId) {
        Review review = reviewRepository.getOrThrow(reviewId);
        validateReviewOwner(review, memberId);
        reviewRepository.delete(review);
    }

    private void validateReviewOwner(Review review, Long memberId) {
        if (!review.isOwner(memberId)) {
            throw new IllegalStateException("해당 리뷰를 수정할 권한이 없습니다.");
        }
    }

    private void validateEventParticipation(Long memberId, Long eventId){
        boolean isUsed = reservationRepository.findTicketStatusUsedByMemberId(memberId).stream()
                .anyMatch(reservation -> reservation.getTickets().stream()
                        .anyMatch(ticket -> ticket.getSeat().getSchedule().getEvent().getId().equals(eventId)));
        if (!isUsed) {
            throw new IllegalStateException("리뷰는 관람 완료된 이벤트에 대해서만 작성할 수 있습니다.");
        }
    }

    private void validateAlreadyReviewed(Long memberId, Long eventId){
        boolean isAlreadyReviewed = reviewRepository.findByMemberIdAndEventId(memberId, eventId).isPresent();
        if (isAlreadyReviewed) {
            throw new IllegalStateException("이미 해당 이벤트에 대한 리뷰를 작성하였습니다.");
        }
    }

    private List<ReviewDetailResponse> findDetailReviewsByEvent(Long eventId) {
        return reviewRepository.findByEventId(eventId)
                .stream()
                .map(ReviewDetailResponse::from)
                .toList();
    }

    private static double getAverageRating(List<ReviewDetailResponse> reviews) {
        return reviews.stream()
                .mapToInt(ReviewDetailResponse::rating)
                .average()
                .orElse(0.0);
    }
}
