package org.example.reservationservice.state;

import org.example.reservationservice.entity.ReservationStatus;

public interface ReservationStateHandler {

    ReservationStatus supportedStatus();

    void validateTransition(ReservationStatus targetStatus);
}
