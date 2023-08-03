package co.kirikiri.controller;

import co.kirikiri.service.MemberService;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
        final Long memberId = memberService.join(request);
        return ResponseEntity.created(URI.create("/api/members/" + memberId)).build();
    }
}
