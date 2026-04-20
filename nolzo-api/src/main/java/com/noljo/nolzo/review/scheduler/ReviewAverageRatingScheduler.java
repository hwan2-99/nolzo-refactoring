package com.noljo.nolzo.review.scheduler;

import com.noljo.nolzo.review.application.port.in.ReviewRatingUpdateUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class ReviewAverageRatingScheduler {

    private static final long THIRTY_MINUTES = 10 * 60 * 1000L;
    private final ReviewRatingUpdateUseCase reviewRatingUpdateUseCase;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeEventAverageRatings() {
        updateEventAverageRate();
    }

    @Scheduled(fixedRate = THIRTY_MINUTES)
    public void updateEventAverageRate() {
        reviewRatingUpdateUseCase.updateEventAverageRate();
    }
}
