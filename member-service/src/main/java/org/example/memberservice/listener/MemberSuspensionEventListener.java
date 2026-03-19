package org.example.memberservice.listener;

import org.example.memberservice.service.MemberService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MemberSuspensionEventListener {

    private final MemberService memberService;

    public MemberSuspensionEventListener(MemberService memberService) {
        this.memberService = memberService;
    }

    @KafkaListener(topics = "member-suspension-updated", groupId = "member-service")
    public void onMemberSuspensionUpdated(Map<String, Object> event) {
        Long memberId = ((Number) event.get("memberId")).longValue();
        boolean suspended = Boolean.parseBoolean(String.valueOf(event.get("suspended")));
        memberService.updateSuspension(memberId, suspended);
    }
}
