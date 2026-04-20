package com.noljo.nolzo.review.application.port.in;

import com.noljo.nolzo.review.dto.request.ReviewCreateRequest;
import com.noljo.nolzo.review.dto.request.ReviewUpdateRequest;
import com.noljo.nolzo.review.dto.response.EventReviewPageResponse;
import com.noljo.nolzo.review.dto.response.ReviewResponse;
import com.noljo.nolzo.review.dto.response.ReviewUpdateResponse;
import java.util.List;

public interface ReviewUseCase {

    ReviewResponse create(Long memberId, ReviewCreateRequest request);

    ReviewUpdateResponse update(Long memberId, Long reviewId, ReviewUpdateRequest request);

    ReviewResponse getReview(Long reviewId);

    List<ReviewResponse> getReviewsByMemberId(Long memberId);

    ReviewResponse getReviewByMemberIdAndEventId(Long memberId, Long eventId);

    EventReviewPageResponse getPagingReviewsByEventId(Long eventId, int page, int size);

    void delete(Long memberId, Long reviewId);
}
