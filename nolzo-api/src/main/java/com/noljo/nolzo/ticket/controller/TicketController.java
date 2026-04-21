package com.noljo.nolzo.ticket.controller;

import com.noljo.nolzo.ticket.dto.TicketResponse;
import com.noljo.nolzo.ticket.application.port.in.TicketUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketUseCase ticketUseCase;

    //todo 회원정보 어떻게 넘겨주는지에 따라 파라미터 추후 수정
    @GetMapping
    public ResponseEntity<List<TicketResponse>> findTickets(Long memberId) {
        List<TicketResponse> response = ticketUseCase.findTickets(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> findTicket(@PathVariable Long ticketId) {
        TicketResponse response = ticketUseCase.findTicket(ticketId);
        return ResponseEntity.ok(response);
    }
}
