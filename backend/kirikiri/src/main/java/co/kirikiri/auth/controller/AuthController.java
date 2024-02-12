package co.kirikiri.auth.controller;

import co.kirikiri.auth.service.AuthService;
import co.kirikiri.auth.service.NaverOauthService;
import co.kirikiri.auth.service.dto.response.OauthRedirectResponse;
import co.kirikiri.auth.service.dto.request.LoginRequest;
import co.kirikiri.auth.service.dto.request.ReissueTokenRequest;
import co.kirikiri.auth.service.dto.response.AuthenticationResponse;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<OauthRedirectResponse> loginOauth() {
        final OauthRedirectResponse oauthRedirectResponse = naverOauthService.makeOauthUrl();
        return ResponseEntity.ok(oauthRedirectResponse);
    }

    @GetMapping("/login/oauth")
    public ResponseEntity<AuthenticationResponse> loginOauth(
            @RequestParam(value = "code") final String code,
            @RequestParam("state") final String state) {
        final Map<String, String> queryParams = Map.of(
                "code", code,
                "state", state,
                "grant_type", "authorization_code"
        );
        final AuthenticationResponse response = naverOauthService.login(queryParams);
        return ResponseEntity.ok(response);
    }
}
