package org.example.reservationservice.repository;

import org.example.reservationservice.entity.Reservation;
import org.example.reservationservice.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByMemberId(Long memberId);

    List<Reservation> findByRoomId(Long roomId);

    List<Reservation> findByMemberIdAndStatus(Long memberId, ReservationStatus status);

    List<Reservation> findByRoomIdAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
            Long roomId,
            ReservationStatus status,
            LocalDateTime endDateTime,
            LocalDateTime startDateTime
    );
}