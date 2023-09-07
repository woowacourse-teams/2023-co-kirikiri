package co.kirikiri.controller;

import co.kirikiri.common.interceptor.Authenticated;
import co.kirikiri.common.resolver.MemberIdentifier;
import co.kirikiri.service.MemberService;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.member.response.MemberInformationForPublicResponse;
import co.kirikiri.service.dto.member.response.MemberInformationResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<Void> join(@RequestBody @Valid final MemberJoinRequest request) {
        final boolean isAdmin = false;
        final Long memberId = memberService.join(request, isAdmin);
        return ResponseEntity.created(URI.create("/api/members/" + memberId)).build();
    }

    @PostMapping("/join/admin")
    public ResponseEntity<Void> joinAdmin(@RequestBody @Valid final MemberJoinRequest request) {
        final boolean isAdmin = true;
        final Long memberId = memberService.join(request, isAdmin);
        return ResponseEntity.created(URI.create("/api/members/" + memberId)).build();
    }

    @GetMapping("/me")
    @Authenticated
    public ResponseEntity<MemberInformationResponse> findMemberInformation(@MemberIdentifier final String identifier) {
        final MemberInformationResponse response = memberService.findMemberInformation(identifier);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{memberId}")
    @Authenticated
    public ResponseEntity<MemberInformationForPublicResponse> findMemberInfo(@PathVariable final Long memberId) {
        final MemberInformationForPublicResponse response = memberService.findMemberInformationForPublic(memberId);
        return ResponseEntity.ok(response);
    }
}
