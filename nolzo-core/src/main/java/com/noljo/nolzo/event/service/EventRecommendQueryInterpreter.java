package com.noljo.nolzo.event.service;

import com.noljo.nolzo.event.dto.EventRecommendCondition;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class EventRecommendQueryInterpreter {

    private static final Pattern MAX_PRICE_PATTERN = Pattern.compile("(\\d+)\\s*만원\\s*이하");

    private static final Map<String, String> REGION_KEYWORDS = Map.of(
            "서울", "서울",
            "부산", "부산",
            "대구", "대구",
            "인천", "인천",
            "광주", "광주",
            "대전", "대전"
    );

    private static final Map<String, String> CATEGORY_KEYWORDS = Map.of(
            "뮤지컬", "MUSICAL",
            "콘서트", "CONCERT",
            "연극", "PLAY"
    );

    private static final Map<String, String> MOOD_KEYWORDS = Map.of(
            "데이트", "데이트",
            "가족", "가족",
            "혼자", "혼자"
    );

    public EventRecommendCondition interpret(String query) {
        String normalizedQuery = query == null ? "" : query.trim();

        return new EventRecommendCondition(
                extractRegion(normalizedQuery),
                extractDateRange(normalizedQuery),
                extractMaxPrice(normalizedQuery),
                extractCategory(normalizedQuery),
                extractMood(normalizedQuery)
        );
    }

    private String extractRegion(String query) {
        return REGION_KEYWORDS.entrySet().stream()
                .filter(entry -> query.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private String extractDateRange(String query) {
        if (query.contains("이번 주말")) {
            return "이번 주말";
        }

        if (query.contains("이번 주")) {
            return "이번 주";
        }

        if (query.contains("오늘")) {
            return "오늘";
        }

        return null;
    }

    private Integer extractMaxPrice(String query) {
        Matcher matcher = MAX_PRICE_PATTERN.matcher(query);
        if (!matcher.find()) {
            return null;
        }

        return Integer.parseInt(matcher.group(1)) * 10_000;
    }

    private String extractCategory(String query) {
        return CATEGORY_KEYWORDS.entrySet().stream()
                .filter(entry -> query.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private String extractMood(String query) {
        return MOOD_KEYWORDS.entrySet().stream()
                .filter(entry -> query.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }
}
