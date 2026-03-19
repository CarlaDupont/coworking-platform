package org.example.reservationservice.controller;

import org.example.reservationservice.entity.Reservation;
import org.example.reservationservice.service.ReservationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
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
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'id : " + id));
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

    @PutMapping("/{id}/cancel")
    public Reservation cancelReservation(@PathVariable Long id) {
        return reservationService.cancelReservation(id);
    }

    @PutMapping("/{id}/complete")
    public Reservation completeReservation(@PathVariable Long id) {
        return reservationService.completeReservation(id);
    }
}