package com.noljo.nolzo.ticket.dto;

import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.ticket.entity.Ticket;
import com.noljo.nolzo.ticket.entity.TicketStatus;
import lombok.Builder;

@Builder
public record TicketResponse(
        Long ticketId,
        TicketStatus ticketStatus,
        String eventTitle,
        String rowName,
        int seatNumber,
        String seatSection
) {
    public static TicketResponse from(Ticket ticket) {
        return TicketResponse.builder()
                .ticketId(ticket.getId())
                .ticketStatus(ticket.getStatus())
                .eventTitle("test") // 해당부분 구현 기다림
                .rowName(ticket.getSeat().getRowName())
                .seatNumber(ticket.getSeat().getSeatNumber())
                .seatSection(ticket.getSeat().getSeatSection())
                .build();
    }
}
