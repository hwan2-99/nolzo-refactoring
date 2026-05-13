package com.noljo.nolzo.event.service;

import com.noljo.nolzo.event.application.port.out.EventPersistencePort;
import com.noljo.nolzo.event.dto.EventRecommendCondition;
import com.noljo.nolzo.event.dto.EventRecommendItemResponse;
import com.noljo.nolzo.event.dto.EventRecommendRequest;
import com.noljo.nolzo.event.dto.EventRecommendResponse;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.seat.entity.Seat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventRecommendService {

    private final EventPersistencePort eventPersistencePort;
    private final EventRecommendQueryInterpreter eventRecommendQueryInterpreter;
    private final EventRecommendReasonGenerator eventRecommendReasonGenerator;

    @Transactional(readOnly = true)
    public EventRecommendResponse recommend(EventRecommendRequest request) {
        EventRecommendCondition condition = eventRecommendQueryInterpreter.interpret(request.query());
        List<EventRecommendItemResponse> recommendations = findRecommendationCandidates(condition).stream()
                .limit(5)
                .map(candidate -> EventRecommendItemResponse.of(
                        candidate.event(),
                        eventRecommendReasonGenerator.generate(
                                candidate.event(),
                                condition,
                                candidate.minimumPrice()
                        )
                ))
                .toList();

        return EventRecommendResponse.of(
                request.query(),
                condition,
                recommendations
        );
    }

    private List<EventRecommendationCandidate> findRecommendationCandidates(EventRecommendCondition condition) {
        List<Event> events = hasSearchCondition(condition)
                ? eventPersistencePort.findAll()
                : eventPersistencePort.findTop6ByOrderByViewCountDesc();

        return events.stream()
                .map(event -> new EventRecommendationCandidate(event, getMinimumSeatPrice(event)))
                .filter(candidate -> matches(candidate, condition))
                .sorted(Comparator
                        .comparingInt((EventRecommendationCandidate candidate) -> calculateScore(candidate, condition))
                        .reversed()
                        .thenComparing(Comparator.comparingLong(
                                (EventRecommendationCandidate candidate) -> candidate.event().getViewCount()
                        ).reversed())
                        .thenComparing(
                                candidate -> candidate.event().getStartDate(),
                                Comparator.nullsLast(Comparator.naturalOrder())
                        ))
                .toList();
    }

    private boolean hasSearchCondition(EventRecommendCondition condition) {
        return condition.region() != null
                || condition.dateRange() != null
                || condition.maxPrice() != null
                || condition.category() != null
                || condition.mood() != null;
    }

    private boolean matches(EventRecommendationCandidate candidate, EventRecommendCondition condition) {
        Event event = candidate.event();

        if (condition.region() != null && !containsIgnoreCase(event.getVenue(), condition.region())) {
            return false;
        }

        if (condition.category() != null && !condition.category().equals(event.getEventCategory().name())) {
            return false;
        }

        if (condition.dateRange() != null && !matchesDateRange(event, condition.dateRange())) {
            return false;
        }

        if (condition.maxPrice() != null) {
            return candidate.minimumPrice() != null && candidate.minimumPrice() <= condition.maxPrice();
        }

        return true;
    }

    private boolean matchesDateRange(Event event, String dateRange) {
        LocalDate today = LocalDate.now();

        return switch (dateRange) {
            case "오늘" -> overlaps(event, today, today);
            case "이번 주" -> overlaps(
                    event,
                    today.with(DayOfWeek.MONDAY),
                    today.with(DayOfWeek.SUNDAY)
            );
            case "이번 주말" -> overlaps(
                    event,
                    today.with(DayOfWeek.SATURDAY),
                    today.with(DayOfWeek.SUNDAY)
            );
            default -> true;
        };
    }

    private boolean overlaps(Event event, LocalDate from, LocalDate to) {
        if (event.getStartDate() == null || event.getEndDate() == null) {
            return false;
        }

        return !event.getEndDate().isBefore(from) && !event.getStartDate().isAfter(to);
    }

    private Integer getMinimumSeatPrice(Event event) {
        OptionalInt minimumPrice = event.getSchedules().stream()
                .filter(Objects::nonNull)
                .map(Schedule::getSeats)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .mapToInt(Seat::getPrice)
                .min();

        return minimumPrice.isPresent() ? minimumPrice.getAsInt() : null;
    }

    private int calculateScore(EventRecommendationCandidate candidate, EventRecommendCondition condition) {
        Event event = candidate.event();
        int score = 0;

        if (condition.region() != null && containsIgnoreCase(event.getVenue(), condition.region())) {
            score += 3;
        }

        if (condition.category() != null && condition.category().equals(event.getEventCategory().name())) {
            score += 3;
        }

        if (condition.dateRange() != null && matchesDateRange(event, condition.dateRange())) {
            score += 2;
        }

        if (condition.maxPrice() != null && candidate.minimumPrice() != null
                && candidate.minimumPrice() <= condition.maxPrice()) {
            score += 2;
        }

        if (condition.mood() != null) {
            score += 1;
        }

        if (!hasSearchCondition(condition)) {
            score += 1;
        }

        return score;
    }

    private boolean containsIgnoreCase(String text, String keyword) {
        if (text == null || keyword == null) {
            return false;
        }

        return text.toLowerCase().contains(keyword.toLowerCase());
    }

    private record EventRecommendationCandidate(Event event, Integer minimumPrice) {
    }
}
