package com.noljo.nolzo.review.controller;

import com.noljo.nolzo.auth.security.CustomUserDetails;
import com.noljo.nolzo.review.dto.request.ReviewUpdateRequest;
import com.noljo.nolzo.review.dto.response.ReviewUpdateResponse;
import com.noljo.nolzo.review.dto.request.ReviewCreateRequest;
import com.noljo.nolzo.review.dto.response.ReviewResponse;
import com.noljo.nolzo.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
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

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewUpdateResponse> updateReview(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequest request) {
        ReviewUpdateResponse response = reviewService.update(user.getMemberId(), reviewId, request);

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@AuthenticationPrincipal CustomUserDetails user, @Valid @RequestBody ReviewCreateRequest request) {
        ReviewResponse response = reviewService.create(user.getMemberId(), request);
        return ResponseEntity.ok(response);
    }
}
