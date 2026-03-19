package org.example.roomservice.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RoomEventProducer {

    private static final String ROOM_DELETED_TOPIC = "room-deleted";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public RoomEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishRoomDeleted(Long roomId) {
        Map<String, Object> event = new HashMap<>();
        event.put("roomId", roomId);
        kafkaTemplate.send(ROOM_DELETED_TOPIC, event);
    }
}
