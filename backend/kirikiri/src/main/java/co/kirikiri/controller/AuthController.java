package co.kirikiri.controller;

import co.kirikiri.service.AuthService;
import co.kirikiri.service.dto.auth.request.AuthenticateResponse;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticateResponse> login(@RequestBody @Valid final LoginRequest request) {
        final AuthenticateResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
