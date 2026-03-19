package org.example.memberservice.controller;

import org.example.memberservice.entity.Member;
import org.example.memberservice.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping({"/members", "/api/members"})
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public List<Member> getAllMembers() {
        return memberService.getAllMembers();
    }

    @GetMapping("/{id}")
    public Member getMemberById(@PathVariable Long id) {
        return memberService.getMemberById(id)
                .orElseThrow(() -> new NoSuchElementException("Member not found"));
    }

    @PostMapping
    public Member createMember(@RequestBody Member member) {
        return memberService.createMember(member);
    }

    @PutMapping("/{id}")
    public Member updateMember(@PathVariable Long id, @RequestBody Member member) {
        return memberService.updateMember(id, member);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
    }

    @GetMapping("/{id}/can-reserve")
    public boolean canReserve(@PathVariable Long id) {
        return memberService.canReserve(id);
    }

    @RequestMapping(value = "/{id}/suspension", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public Member updateSuspension(@PathVariable Long id, @RequestParam boolean suspended) {
        return memberService.updateSuspension(id, suspended);
    }
}