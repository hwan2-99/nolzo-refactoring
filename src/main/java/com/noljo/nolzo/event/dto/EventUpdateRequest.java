package com.noljo.nolzo.event.dto;

import com.noljo.nolzo.event.entity.Event;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateRequest {
    @NotBlank(message = "제목 입력 필수")
    private String title;

    @NotBlank(message = "장소 입력 필수")
    private String venue;

    @NotBlank(message = "설명 입력 필수")
    private String description;

    @NotNull(message = "시작 입력 필수")
    private LocalDate startDate;

    @NotNull(message = "종료 입력 필수")
    private LocalDate endDate;

    public Event toEntity(Event original) {
        return Event.builder()
                .id(original.getId())
                .title(this.title)
                .venue(this.venue)
                .description(this.description)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .build();
    }
}