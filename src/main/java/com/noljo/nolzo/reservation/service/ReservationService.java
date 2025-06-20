package com.noljo.nolzo.reservation.service;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.repository.EventRepository;
import com.noljo.nolzo.reservation.dto.ReservationEventInfo;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;

    public List<ReservationEventInfo> findReservations(Long memberId) {

        List<Reservation> reservationList = reservationRepository.findReservationsByMemberId(memberId);

        return reservationList.stream()
                .map(reservation ->{
                    Event event = reservation.getTickets().get(0).getSeat().getEvent();
                        return ReservationEventInfo.of(event,reservation);
                        }
                        )
                .toList();
    }
}
