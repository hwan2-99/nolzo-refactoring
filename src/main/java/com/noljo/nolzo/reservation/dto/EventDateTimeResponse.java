    package com.noljo.nolzo.reservation.dto;

    import com.noljo.nolzo.Schedule.entity.Schedule;
    import lombok.Getter;

    import java.time.LocalDate;
    import java.time.LocalTime;

    @Getter
    public class EventDateTimeResponse {

        private Long id;
        private LocalDate showDate;
        private LocalTime showTime;

        private EventDateTimeResponse(Long id, LocalDate showDate, LocalTime showTime) {
            this.id = id;
            this.showDate = showDate;
            this.showTime = showTime;
        }

        public static EventDateTimeResponse fromSchedule(Schedule schedule) {
            return new EventDateTimeResponse(
                    schedule.getEvent().getId(),
                    schedule.getShowDate(),
                    schedule.getShowTime()
            );
        }
    }
