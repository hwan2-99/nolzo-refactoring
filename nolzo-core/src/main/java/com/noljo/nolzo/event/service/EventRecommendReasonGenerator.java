package com.noljo.nolzo.event.service;

import com.noljo.nolzo.event.dto.EventRecommendCondition;
import com.noljo.nolzo.event.entity.Event;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class EventRecommendReasonGenerator {

    public String generate(Event event, EventRecommendCondition condition, Integer minimumPrice) {
        List<String> reasons = new ArrayList<>();

        if (condition.region() != null && containsIgnoreCase(event.getVenue(), condition.region())) {
            reasons.add(condition.region() + "에서 관람할 수 있는 공연입니다.");
        }

        if (condition.category() != null && condition.category().equals(event.getEventCategory().name())) {
            reasons.add(toCategoryLabel(condition.category()) + " 장르를 찾는 조건과 잘 맞습니다.");
        }

        if (condition.dateRange() != null) {
            reasons.add(condition.dateRange() + " 일정에 맞춰 관람할 수 있습니다.");
        }

        if (condition.maxPrice() != null && minimumPrice != null && minimumPrice <= condition.maxPrice()) {
            reasons.add("최저 좌석가가 " + formatPrice(minimumPrice) + "부터라 예산 조건에 맞습니다.");
        }

        if (condition.mood() != null) {
            reasons.add(toMoodReason(condition.mood()));
        }

        if (reasons.isEmpty()) {
            reasons.add("현재 조건과 가까운 공연으로 추천합니다.");
        }

        return String.join(" ", reasons);
    }

    public String generateFallback(Event event) {
        return "입력한 조건과 정확히 일치하는 공연이 없어 현재 인기 있거나 먼저 살펴볼 만한 공연으로 추천합니다.";
    }

    private String toCategoryLabel(String category) {
        return switch (category) {
            case "MUSICAL" -> "뮤지컬";
            case "CONCERT" -> "콘서트";
            case "PLAY" -> "연극";
            default -> category;
        };
    }

    private String toMoodReason(String mood) {
        return switch (mood) {
            case "데이트" -> "데이트 코스로 고려하기 좋은 분위기의 공연입니다.";
            case "가족" -> "가족과 함께 관람하기 좋은 공연으로 추천합니다.";
            case "혼자" -> "혼자 편하게 관람하기 좋은 공연으로 추천합니다.";
            default -> mood + " 목적에 맞는 공연으로 추천합니다.";
        };
    }

    private String formatPrice(int price) {
        return String.format("%,d원", price);
    }

    private boolean containsIgnoreCase(String text, String keyword) {
        if (text == null || keyword == null) {
            return false;
        }

        return text.toLowerCase().contains(keyword.toLowerCase());
    }
}
