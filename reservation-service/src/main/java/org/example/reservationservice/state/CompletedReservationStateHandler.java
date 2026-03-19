package org.example.reservationservice.state;

import org.example.reservationservice.entity.ReservationStatus;
import org.springframework.stereotype.Component;

@Component
public class CompletedReservationStateHandler implements ReservationStateHandler {

    @Override
    public ReservationStatus supportedStatus() {
        return ReservationStatus.COMPLETED;
    }

    @Override
    public void validateTransition(ReservationStatus targetStatus) {
        throw new IllegalStateException("A completed reservation cannot transition again");
    }
}
