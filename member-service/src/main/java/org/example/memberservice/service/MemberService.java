package org.example.memberservice.service;

import org.example.memberservice.entity.Member;
import org.example.memberservice.entity.SubscriptionType;
import org.example.memberservice.producer.MemberEventProducer;
import org.example.memberservice.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberEventProducer memberEventProducer;

    public MemberService(MemberRepository memberRepository, MemberEventProducer memberEventProducer) {
        this.memberRepository = memberRepository;
        this.memberEventProducer = memberEventProducer;
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    public Member createMember(Member member) {
        member.setSuspended(false);
        member.setMaxConcurrentBookings(determineMaxBookings(member.getSubscriptionType()));
        return memberRepository.save(member);
    }

    public Member updateMember(Long id, Member updatedMember) {
        return memberRepository.findById(id)
                .map(member -> {
                    member.setFullName(updatedMember.getFullName());
                    member.setEmail(updatedMember.getEmail());
                    member.setSubscriptionType(updatedMember.getSubscriptionType());
                    member.setSuspended(updatedMember.isSuspended());
                    member.setMaxConcurrentBookings(determineMaxBookings(updatedMember.getSubscriptionType()));
                    return memberRepository.save(member);
                })
                .orElseThrow(() -> new NoSuchElementException("Member not found"));
    }

    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Member not found"));

        memberRepository.delete(member);
        memberEventProducer.publishMemberDeleted(id);
    }

    public boolean canReserve(Long id) {
        return memberRepository.findById(id)
                .map(member -> !member.isSuspended())
                .orElse(false);
    }

    public Member updateSuspension(Long id, boolean suspended) {
        return memberRepository.findById(id)
                .map(member -> {
                    member.setSuspended(suspended);
                    return memberRepository.save(member);
                })
                .orElseThrow(() -> new NoSuchElementException("Member not found"));
    }

    private int determineMaxBookings(SubscriptionType subscriptionType) {
        return switch (subscriptionType) {
            case BASIC -> 2;
            case PRO -> 5;
            case ENTERPRISE -> 10;
        };
    }
}