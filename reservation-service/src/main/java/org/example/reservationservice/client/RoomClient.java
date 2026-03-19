package org.example.reservationservice.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class RoomClient {

    private final RestTemplate restTemplate;

    public RoomClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public RoomDto getRoomById(Long roomId) {
        try {
            return restTemplate.getForObject(
                    "http://room-service/rooms/" + roomId,
                    RoomDto.class
            );
        } catch (HttpClientErrorException.NotFound exception) {
            return null;
        }
    }

    public boolean isRoomAvailable(Long roomId) {
        Boolean response = restTemplate.getForObject(
                "http://room-service/rooms/" + roomId + "/availability",
                Boolean.class
        );
        return Boolean.TRUE.equals(response);
    }

    public void updateRoomAvailability(Long roomId, boolean available) {
        restTemplate.put(
                "http://room-service/rooms/" + roomId + "/availability?available=" + available,
                null
        );
    }
}