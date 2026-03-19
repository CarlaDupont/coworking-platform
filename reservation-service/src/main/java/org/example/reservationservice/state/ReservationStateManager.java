package org.example.reservationservice.state;

import org.example.reservationservice.entity.Reservation;
import org.example.reservationservice.entity.ReservationStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class ReservationStateManager {

    private final Map<ReservationStatus, ReservationStateHandler> handlers = new EnumMap<>(ReservationStatus.class);

    public ReservationStateManager(List<ReservationStateHandler> stateHandlers) {
        for (ReservationStateHandler stateHandler : stateHandlers) {
            handlers.put(stateHandler.supportedStatus(), stateHandler);
        }
    }

    public void transitionTo(Reservation reservation, ReservationStatus targetStatus) {
        ReservationStateHandler currentStateHandler = handlers.get(reservation.getStatus());
        if (currentStateHandler == null) {
            throw new IllegalStateException("Unsupported reservation status transition");
        }

        currentStateHandler.validateTransition(targetStatus);
        reservation.setStatus(targetStatus);
    }
}
