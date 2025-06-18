package com.noljo.nolzo.reservation.service;

import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.reservation.dto.ReservationRequest;
import com.noljo.nolzo.reservation.dto.ReservationResponse;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.entity.ReservationStatus;
import com.noljo.nolzo.reservation.repository.ReservationRepository;
import com.noljo.nolzo.seat.service.SeatService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class ReservationService {
    private static final String RESERVATION_NUMBER_PREFIX = "NOLZO";
    private static final int YEAR_SUFFIX_LENGTH = 2;
    private static final int RESERVATION_NUMBER_COUNT = 1;

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final SeatService seatService;

    //todo Permistic lock을 사용해서 구현한 내용 추후 multi-thread or Optimistic Lock or Redis 사용후 비교예정
    public ReservationResponse create(Long memberId, ReservationRequest request) {
        Member member = memberRepository.getOrThrow(memberId);
        Reservation reservation = new Reservation(ReservationStatus.PENDING, request.calculateTotalPrice(),
                createReservationNumber(), member);

        seatService.updateWithReservation(request.seats());
        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    private String createReservationNumber() {
        String yearSuffix = String.valueOf(LocalDate.now().getYear()).substring(YEAR_SUFFIX_LENGTH);
        int reservationNumber = reservationRepository.findAll().size() + RESERVATION_NUMBER_COUNT;
        String reservationId = String.format("%05d", reservationNumber);

        return RESERVATION_NUMBER_PREFIX + yearSuffix + reservationId;
    }
}
