package co.kirikiri.controller;

import co.kirikiri.service.AuthService;
import co.kirikiri.service.NaverOauthService;
import co.kirikiri.service.dto.auth.OauthRedirectResponse;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.request.ReissueTokenRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final NaverOauthService naverOauthService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid final LoginRequest request) {
        final AuthenticationResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reissue")
    public ResponseEntity<AuthenticationResponse> reissue(@RequestBody @Valid final ReissueTokenRequest request) {
        final AuthenticationResponse response = authService.reissueToken(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/oauth/naver")
    public ResponseEntity<Void> loginOauth() {
        final OauthRedirectResponse oauthRedirectResponse = naverOauthService.makeOauthUrl();
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(oauthRedirectResponse.url()))
                .build();
    }

    @GetMapping("/login/oauth")
    public ResponseEntity<AuthenticationResponse> loginOauth(
            @RequestParam(value = "code", required = false) final String code,
            @RequestParam("state") final String state) {
        final Map<String, String> headers = Map.of(
                "code", code,
                "state", state,
                "grant_type", "authorization_code"
        );
        final AuthenticationResponse response = naverOauthService.login(headers);
        return ResponseEntity.ok(response);
    }
}
