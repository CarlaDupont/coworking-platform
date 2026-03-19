package org.example.reservationservice.service;

import org.example.reservationservice.client.MemberClient;
import org.example.reservationservice.client.MemberDto;
import org.example.reservationservice.client.RoomClient;
import org.example.reservationservice.client.RoomDto;
import org.example.reservationservice.entity.Reservation;
import org.example.reservationservice.entity.ReservationStatus;
import org.example.reservationservice.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomClient roomClient;
    private final MemberClient memberClient;

    public ReservationService(ReservationRepository reservationRepository,
                              RoomClient roomClient,
                              MemberClient memberClient) {
        this.reservationRepository = reservationRepository;
        this.roomClient = roomClient;
        this.memberClient = memberClient;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public List<Reservation> getReservationsByMemberId(Long memberId) {
        return reservationRepository.findByMemberId(memberId);
    }

    public List<Reservation> getReservationsByRoomId(Long roomId) {
        return reservationRepository.findByRoomId(roomId);
    }

    public Reservation createReservation(Reservation reservation) {
        RoomDto room = roomClient.getRoomById(reservation.getRoomId());
        if (room == null) {
            throw new RuntimeException("Salle introuvable");
        }

        MemberDto member = memberClient.getMemberById(reservation.getMemberId());
        if (member == null) {
            throw new RuntimeException("Membre introuvable");
        }

        boolean roomAvailable = roomClient.isRoomAvailable(reservation.getRoomId());
        if (!roomAvailable) {
            throw new RuntimeException("Salle non disponible");
        }

        boolean canReserve = memberClient.canReserve(reservation.getMemberId());
        if (!canReserve) {
            throw new RuntimeException("Membre suspendu");
        }

        List<Reservation> conflicts = reservationRepository
                .findByRoomIdAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                        reservation.getRoomId(),
                        ReservationStatus.CONFIRMED,
                        reservation.getEndDateTime(),
                        reservation.getStartDateTime()
                );

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Conflit de réservation sur ce créneau");
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        Reservation savedReservation = reservationRepository.save(reservation);

        roomClient.updateRoomAvailability(reservation.getRoomId(), false);

        return savedReservation;
    }

    public Reservation cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation savedReservation = reservationRepository.save(reservation);

        roomClient.updateRoomAvailability(reservation.getRoomId(), true);

        return savedReservation;
    }

    public Reservation completeReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        reservation.setStatus(ReservationStatus.COMPLETED);
        Reservation savedReservation = reservationRepository.save(reservation);

        roomClient.updateRoomAvailability(reservation.getRoomId(), true);

        return savedReservation;
    }
}