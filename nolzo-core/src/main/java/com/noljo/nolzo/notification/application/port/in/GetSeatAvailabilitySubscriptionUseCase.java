package com.noljo.nolzo.notification.application.port.in;

import com.noljo.nolzo.notification.dto.SeatAvailabilitySubscriptionResponse;
import java.util.List;

public interface GetSeatAvailabilitySubscriptionUseCase {

    List<SeatAvailabilitySubscriptionResponse> readAllByMemberId(Long memberId);
}
