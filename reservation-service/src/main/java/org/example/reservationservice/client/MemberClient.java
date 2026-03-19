package org.example.reservationservice.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class MemberClient {

    private final RestTemplate restTemplate;

    public MemberClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MemberDto getMemberById(Long memberId) {
        try {
            return restTemplate.getForObject(
                    "http://member-service/members/" + memberId,
                    MemberDto.class
            );
        } catch (HttpClientErrorException.NotFound exception) {
            return null;
        }
    }

    public boolean canReserve(Long memberId) {
        Boolean response = restTemplate.getForObject(
                "http://member-service/members/" + memberId + "/can-reserve",
                Boolean.class
        );
        return Boolean.TRUE.equals(response);
    }
}