package org.example.reservationservice.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ReservationEventProducer {

    private static final String MEMBER_SUSPENSION_UPDATED_TOPIC = "member-suspension-updated";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ReservationEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishMemberSuspensionUpdated(Long memberId, boolean suspended) {
        Map<String, Object> event = new HashMap<>();
        event.put("memberId", memberId);
        event.put("suspended", suspended);
        kafkaTemplate.send(
                MEMBER_SUSPENSION_UPDATED_TOPIC,
                event
        );
    }
}
