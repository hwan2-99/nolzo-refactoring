package com.noljo.nolzo.review.controller;

import com.noljo.nolzo.auth.security.CustomUserDetails;
import com.noljo.nolzo.review.dto.request.ReviewUpdateRequest;
import com.noljo.nolzo.review.dto.response.ReviewUpdateResponse;
import com.noljo.nolzo.review.dto.request.ReviewCreateRequest;
import com.noljo.nolzo.review.dto.response.ReviewResponse;
import com.noljo.nolzo.review.service.ReviewService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Transactional(readOnly = true)
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable Long reviewId) {
        ReviewResponse response = reviewService.getReview(reviewId);
        return ResponseEntity.ok(response);
    }

    @Transactional(readOnly = true)
    @GetMapping("/my")
    public ResponseEntity<List<ReviewResponse>> getMemberReview(@AuthenticationPrincipal(expression = "memberId") Long memberId) {
        List<ReviewResponse> response = reviewService.getReviewsByMemberId(memberId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@AuthenticationPrincipal CustomUserDetails user, @Valid @RequestBody ReviewCreateRequest request) {
        ReviewResponse response = reviewService.create(user.getMemberId(), request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewUpdateResponse> updateReview(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long reviewId, @Valid @RequestBody ReviewUpdateRequest request) {
        ReviewUpdateResponse response = reviewService.update(user.getMemberId(), reviewId, request);
        return ResponseEntity.ok(response);
    }
}
