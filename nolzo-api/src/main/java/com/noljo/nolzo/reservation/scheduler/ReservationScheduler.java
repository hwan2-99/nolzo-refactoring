package com.noljo.nolzo.reservation.scheduler;

import com.noljo.nolzo.queue.application.QueueService;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.entity.ReservationStatus;
import com.noljo.nolzo.reservation.repository.ReservationRepository;
import com.noljo.nolzo.seat.entity.SeatStatus;
import com.noljo.nolzo.seat.service.SeatService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationRepository reservationRepository;
    private final SeatService seatService;
    private final QueueService queueService;

    /**
     * 매 1분마다 실행 → 5분이 지난 PENDING 예약은 자동 취소
     */
    @Scheduled(fixedRate = 60000)
    @SchedulerLock(
            name = "reservationScheduler.cancelUnpaidReservations",
            lockAtMostFor = "PT50S",
            lockAtLeastFor = "PT5S"
    )
    @Transactional
    public void cancelUnpaidReservations() {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(1);
        List<Reservation> overdueReservations =
                reservationRepository.findByStatusAndCreatedAtBefore(ReservationStatus.PENDING, deadline);

        for (Reservation reservation : overdueReservations) {
            log.info("자동 취소 처리: reservationId = {}", reservation.getId());
            seatService.updateWithPayment(reservation.getTickets(), SeatStatus.AVAILABLE);
            reservationRepository.delete(reservation);
        }
    }

    /**
     * 매 1초마다 실행 → 예매 대기열 처리
     */
    @Scheduled(fixedDelay = 1000)
    @SchedulerLock(
            name = "reservationScheduler.processReservationQueues",
            lockAtMostFor = "PT10S",
            lockAtLeastFor = "PT1S"
    )
    public void processReservationQueues() {
        Set<Long> managedEventIds = queueService.getManagedEventIds();

        for (Long eventId : managedEventIds) {
            queueService.processQueue(eventId);

            log.info("예매 대기열 처리 - eventId={}", eventId);
        }
    }
}
