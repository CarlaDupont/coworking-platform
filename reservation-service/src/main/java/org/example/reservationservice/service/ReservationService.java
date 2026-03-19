package org.example.reservationservice.service;

import org.example.reservationservice.client.MemberClient;
import org.example.reservationservice.client.MemberDto;
import org.example.reservationservice.client.RoomClient;
import org.example.reservationservice.client.RoomDto;
import org.example.reservationservice.entity.Reservation;
import org.example.reservationservice.entity.ReservationStatus;
import org.example.reservationservice.producer.ReservationEventProducer;
import org.example.reservationservice.repository.ReservationRepository;
import org.example.reservationservice.state.ReservationStateManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomClient roomClient;
    private final MemberClient memberClient;
    private final ReservationStateManager reservationStateManager;
    private final ReservationEventProducer reservationEventProducer;

    public ReservationService(ReservationRepository reservationRepository,
                              RoomClient roomClient,
                              MemberClient memberClient,
                              ReservationStateManager reservationStateManager,
                              ReservationEventProducer reservationEventProducer) {
        this.reservationRepository = reservationRepository;
        this.roomClient = roomClient;
        this.memberClient = memberClient;
        this.reservationStateManager = reservationStateManager;
        this.reservationEventProducer = reservationEventProducer;
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

    @Transactional
    public Reservation createReservation(Reservation reservation) {
        validateReservationWindow(reservation);

        RoomDto room = roomClient.getRoomById(reservation.getRoomId());
        if (room == null) {
            throw new NoSuchElementException("Room not found");
        }

        MemberDto member = memberClient.getMemberById(reservation.getMemberId());
        if (member == null) {
            throw new NoSuchElementException("Member not found");
        }

        LocalDateTime now = LocalDateTime.now();
        boolean reservationStartsNow = !reservation.getStartDateTime().isAfter(now)
                && reservation.getEndDateTime().isAfter(now);
        boolean roomAvailable = !reservationStartsNow || roomClient.isRoomAvailable(reservation.getRoomId());
        if (!roomAvailable) {
            throw new IllegalStateException("Room is already booked for the selected time slot");
        }

        boolean canReserve = memberClient.canReserve(reservation.getMemberId());
        if (!canReserve) {
            throw new IllegalStateException("Member is suspended");
        }

        long activeReservations = reservationRepository.countByMemberIdAndStatus(
                reservation.getMemberId(),
                ReservationStatus.CONFIRMED
        );
        if (member.getMaxConcurrentBookings() != null
                && activeReservations >= member.getMaxConcurrentBookings()) {
            throw new IllegalStateException("Member is suspended");
        }

        List<Reservation> conflicts = reservationRepository
                .findByRoomIdAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                        reservation.getRoomId(),
                        ReservationStatus.CONFIRMED,
                        reservation.getEndDateTime(),
                        reservation.getStartDateTime()
                );

        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Room is already booked for the selected time slot");
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        Reservation savedReservation = reservationRepository.save(reservation);

        updateRoomAvailabilityForCurrentTime(savedReservation.getRoomId());
        updateMemberSuspensionStatus(savedReservation.getMemberId());

        return savedReservation;
    }

    @Transactional
    public Reservation cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reservation not found"));

        return transitionReservation(reservation, ReservationStatus.CANCELLED);
    }

    @Transactional
    public Reservation completeReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reservation not found"));

        return transitionReservation(reservation, ReservationStatus.COMPLETED);
    }

    @Transactional
    public void cancelReservationsForRoom(Long roomId) {
        List<Reservation> roomReservations = reservationRepository.findByRoomId(roomId);
        for (Reservation reservation : roomReservations) {
            if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
                transitionReservation(reservation, ReservationStatus.CANCELLED);
            }
        }
    }

    @Transactional
    public void deleteReservationsForMember(Long memberId) {
        List<Reservation> memberReservations = reservationRepository.findByMemberId(memberId);
        for (Reservation reservation : memberReservations) {
            reservationRepository.delete(reservation);
            updateRoomAvailabilityForCurrentTime(reservation.getRoomId());
        }
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void completeExpiredReservations() {
        List<Reservation> expiredReservations = reservationRepository.findByStatusAndEndDateTimeBefore(
                ReservationStatus.CONFIRMED,
                LocalDateTime.now()
        );

        for (Reservation reservation : expiredReservations) {
            transitionReservation(reservation, ReservationStatus.COMPLETED);
        }
    }

    private Reservation transitionReservation(Reservation reservation, ReservationStatus targetStatus) {
        reservationStateManager.transitionTo(reservation, targetStatus);
        Reservation savedReservation = reservationRepository.save(reservation);

        updateRoomAvailabilityForCurrentTime(savedReservation.getRoomId());
        updateMemberSuspensionStatus(savedReservation.getMemberId());

        return savedReservation;
    }

    private void updateRoomAvailabilityForCurrentTime(Long roomId) {
        LocalDateTime now = LocalDateTime.now();
        boolean hasOngoingReservation = !reservationRepository
                .findByRoomIdAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                        roomId,
                        ReservationStatus.CONFIRMED,
                        now,
                        now
                )
                .isEmpty();
        try {
            roomClient.updateRoomAvailability(roomId, !hasOngoingReservation);
        } catch (Exception ignored) {
            // La salle peut avoir été supprimée avant le traitement asynchrone de l'événement Kafka.
        }
    }

    private void updateMemberSuspensionStatus(Long memberId) {
        MemberDto member = memberClient.getMemberById(memberId);
        if (member == null || member.getMaxConcurrentBookings() == null) {
            return;
        }

        long activeReservations = reservationRepository.countByMemberIdAndStatus(
                memberId,
                ReservationStatus.CONFIRMED
        );
        boolean shouldBeSuspended = activeReservations >= member.getMaxConcurrentBookings();
        reservationEventProducer.publishMemberSuspensionUpdated(memberId, shouldBeSuspended);
    }

    private void validateReservationWindow(Reservation reservation) {
        if (reservation.getStartDateTime() == null || reservation.getEndDateTime() == null) {
            throw new IllegalArgumentException("start and end dates are required");
        }

        if (!reservation.getEndDateTime().isAfter(reservation.getStartDateTime())) {
            throw new IllegalArgumentException("end date must be after start date");
        }
    }
}