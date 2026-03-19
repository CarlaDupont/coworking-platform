package org.example.memberservice.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MemberEventProducer {

    private static final String MEMBER_DELETED_TOPIC = "member-deleted";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public MemberEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishMemberDeleted(Long memberId) {
        Map<String, Object> event = new HashMap<>();
        event.put("memberId", memberId);
        kafkaTemplate.send(MEMBER_DELETED_TOPIC, event);
    }
}
