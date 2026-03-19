package org.example.reservationservice.state;

import org.example.reservationservice.entity.ReservationStatus;
import org.springframework.stereotype.Component;

@Component
public class CancelledReservationStateHandler implements ReservationStateHandler {

    @Override
    public ReservationStatus supportedStatus() {
        return ReservationStatus.CANCELLED;
    }

    @Override
    public void validateTransition(ReservationStatus targetStatus) {
        throw new IllegalStateException("A cancelled reservation cannot transition again");
    }
}
