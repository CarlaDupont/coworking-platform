package org.example.reservationservice.listener;

import org.example.reservationservice.service.ReservationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReservationEventListener {

    private final ReservationService reservationService;

    public ReservationEventListener(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @KafkaListener(topics = "room-deleted", groupId = "reservation-service")
    public void onRoomDeleted(Map<String, Object> event) {
        Long roomId = ((Number) event.get("roomId")).longValue();
        reservationService.cancelReservationsForRoom(roomId);
    }

    @KafkaListener(topics = "member-deleted", groupId = "reservation-service")
    public void onMemberDeleted(Map<String, Object> event) {
        Long memberId = ((Number) event.get("memberId")).longValue();
        reservationService.deleteReservationsForMember(memberId);
    }
}
