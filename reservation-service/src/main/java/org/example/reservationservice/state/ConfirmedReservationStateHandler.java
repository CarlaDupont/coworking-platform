package org.example.reservationservice.state;

import org.example.reservationservice.entity.ReservationStatus;
import org.springframework.stereotype.Component;

@Component
public class ConfirmedReservationStateHandler implements ReservationStateHandler {

    @Override
    public ReservationStatus supportedStatus() {
        return ReservationStatus.CONFIRMED;
    }

    @Override
    public void validateTransition(ReservationStatus targetStatus) {
        if (targetStatus != ReservationStatus.CANCELLED && targetStatus != ReservationStatus.COMPLETED) {
            throw new IllegalStateException("Invalid transition from CONFIRMED");
        }
    }
}
