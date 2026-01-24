package za.tomvuma.logisticshub.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import za.tomvuma.logisticshub.dto.*;
import za.tomvuma.logisticshub.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService auth;

    public AuthController(AuthService auth) { this.auth = auth; }

    @PostMapping("/register") // MVP: admin passes actor id via header
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest req,
                                                 @RequestHeader(name = "X-Actor-Id", required = false) Long actorId) {
        return ResponseEntity.ok(auth.register(req, actorId));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(auth.login(req));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authn) {
        return ResponseEntity.ok(auth.me(authn.getName()));
    }
}
