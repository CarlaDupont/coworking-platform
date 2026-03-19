package org.example.reservationservice.controller;

import org.example.reservationservice.entity.Reservation;
import org.example.reservationservice.service.ReservationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping({"/reservations", "/api/reservations"})
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/{id}")
    public Reservation getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .orElseThrow(() -> new NoSuchElementException("Reservation not found"));
    }

    @GetMapping("/member/{memberId}")
    public List<Reservation> getReservationsByMemberId(@PathVariable Long memberId) {
        return reservationService.getReservationsByMemberId(memberId);
    }

    @GetMapping("/room/{roomId}")
    public List<Reservation> getReservationsByRoomId(@PathVariable Long roomId) {
        return reservationService.getReservationsByRoomId(roomId);
    }

    @PostMapping
    public Reservation createReservation(@RequestBody Reservation reservation) {
        return reservationService.createReservation(reservation);
    }

    @RequestMapping(value = "/{id}/cancel", method = {RequestMethod.PUT, RequestMethod.POST})
    public Reservation cancelReservation(@PathVariable Long id) {
        return reservationService.cancelReservation(id);
    }

    @RequestMapping(value = "/{id}/complete", method = {RequestMethod.PUT, RequestMethod.POST})
    public Reservation completeReservation(@PathVariable Long id) {
        return reservationService.completeReservation(id);
    }
}