package com.noljo.nolzo.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.schedule.dto.internal.ScheduleInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<ScheduleInfo> schedules;

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
