package com.noljo.nolzo.review.controller;

import com.noljo.nolzo.auth.security.CustomUserDetails;
import com.noljo.nolzo.review.dto.request.ReviewCreateRequest;
import com.noljo.nolzo.review.dto.request.ReviewUpdateRequest;
import com.noljo.nolzo.review.dto.response.EventReviewPageResponse;
import com.noljo.nolzo.review.dto.response.ReviewResponse;
import com.noljo.nolzo.review.dto.response.ReviewUpdateResponse;
import com.noljo.nolzo.review.application.port.in.ReviewUseCase;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewUseCase reviewUseCase;

    @Transactional(readOnly = true)
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable Long reviewId) {
        ReviewResponse response = reviewUseCase.getReview(reviewId);
        return ResponseEntity.ok(response);
    }

    @Transactional(readOnly = true)
    @GetMapping("/my")
    public ResponseEntity<List<ReviewResponse>> getMemberReview(@AuthenticationPrincipal(expression = "memberId") Long memberId) {
        List<ReviewResponse> response = reviewUseCase.getReviewsByMemberId(memberId);
        return ResponseEntity.ok(response);
    }

    @Transactional(readOnly = true)
    @GetMapping("/events/{eventId}/my")
    public ResponseEntity<ReviewResponse> getMyReviewByEvent(
            @AuthenticationPrincipal(expression = "memberId") Long memberId, 
            @PathVariable Long eventId
    ) {
        ReviewResponse response = reviewUseCase.getReviewByMemberIdAndEventId(memberId, eventId);
        return ResponseEntity.ok(response);
    }

    @Transactional(readOnly = true)
    @GetMapping("/events/{eventId}")
    public ResponseEntity<EventReviewPageResponse> getReviewsByEvent(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        EventReviewPageResponse response = reviewUseCase.getPagingReviewsByEventId(eventId, page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody ReviewCreateRequest request
    ) {
        ReviewResponse response = reviewUseCase.create(user.getMemberId(), request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewUpdateResponse> updateReview(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequest request
    ) {
        ReviewUpdateResponse response = reviewUseCase.update(user.getMemberId(), reviewId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long reviewId
    ) {
        reviewUseCase.delete(user.getMemberId(), reviewId);
        return ResponseEntity.noContent().build();
    }
}
