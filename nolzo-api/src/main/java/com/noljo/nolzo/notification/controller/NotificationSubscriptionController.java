package com.noljo.nolzo.notification.controller;

import com.noljo.nolzo.notification.application.port.in.CancelSeatAvailabilitySubscriptionUseCase;
import com.noljo.nolzo.notification.application.port.in.CreateSeatAvailabilitySubscriptionUseCase;
import com.noljo.nolzo.notification.application.port.in.GetSeatAvailabilitySubscriptionUseCase;
import com.noljo.nolzo.notification.dto.CreateSeatAvailabilitySubscriptionRequest;
import com.noljo.nolzo.notification.dto.SeatAvailabilitySubscriptionResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications/subscriptions")
public class NotificationSubscriptionController {

    private final CreateSeatAvailabilitySubscriptionUseCase createSeatAvailabilitySubscriptionUseCase;
    private final CancelSeatAvailabilitySubscriptionUseCase cancelSeatAvailabilitySubscriptionUseCase;
    private final GetSeatAvailabilitySubscriptionUseCase getSeatAvailabilitySubscriptionUseCase;

    @PostMapping
    public ResponseEntity<SeatAvailabilitySubscriptionResponse> create(
            @AuthenticationPrincipal(expression = "memberId") Long memberId,
            @RequestBody CreateSeatAvailabilitySubscriptionRequest request
    ) {
        return ResponseEntity.ok(
                createSeatAvailabilitySubscriptionUseCase.create(memberId, request)
        );
    }

    @GetMapping
    public ResponseEntity<List<SeatAvailabilitySubscriptionResponse>> readAll(
            @AuthenticationPrincipal(expression = "memberId") Long memberId
    ) {
        return ResponseEntity.ok(
                getSeatAvailabilitySubscriptionUseCase.readAllByMemberId(memberId)
        );
    }

    @DeleteMapping("/{subscriptionId}")
    public ResponseEntity<Void> cancel(
            @AuthenticationPrincipal(expression = "memberId") Long memberId,
            @PathVariable Long subscriptionId
    ) {
        cancelSeatAvailabilitySubscriptionUseCase.cancel(memberId, subscriptionId);
        return ResponseEntity.noContent().build();
    }
}
