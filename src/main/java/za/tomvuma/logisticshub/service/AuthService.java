package za.tomvuma.logisticshub.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import za.tomvuma.logisticshub.dto.*;
import za.tomvuma.logisticshub.entity.Role;
import za.tomvuma.logisticshub.entity.User;
import za.tomvuma.logisticshub.repository.RoleRepository;
import za.tomvuma.logisticshub.repository.UserRepository;
import za.tomvuma.logisticshub.security.JwtUtil;
import jakarta.transaction.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;
    private final AuditService audit;

    // Allowed roles for normal users (ADMIN excluded)
    private static final Set<String> ALLOWED_ROLES = Set.of("SUPPLIER", "WAREHOUSE_MANAGER", "CUSTOMER");

    public AuthService(UserRepository users, RoleRepository roles, PasswordEncoder encoder, JwtUtil jwt, AuditService audit) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
        this.jwt = jwt;
        this.audit = audit;
    }

    @Transactional
    public UserResponse register(RegisterRequest req, Long adminActorId) {

        if (users.existsByUsername(req.username())) {
            throw new IllegalArgumentException("Username already taken");
        }

        if (users.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        var user = new User();
        user.setUsername(req.username()); // ✅ NEW
        user.setEmail(req.email());
        user.setPasswordHash(encoder.encode(req.password()));

        var requestedRoles = (req.roles() == null || req.roles().isEmpty())
                ? Set.of("CUSTOMER")
                : req.roles();

        var validRoles = requestedRoles.stream()
                .filter(ALLOWED_ROLES::contains)
                .map(roles::findByName)
                .collect(Collectors.toSet());

        user.setRoles(validRoles);

        var saved = users.save(user);

        audit.record(
                adminActorId,
                "REGISTER_USER",
                "User",
                saved.getId(),
                "{\"username\":\"" + saved.getUsername() + "\",\"email\":\"" + saved.getEmail() + "\"}"
        );

        return new UserResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getRoles().stream().map(Role::getName).collect(Collectors.toSet()),
                saved.getStatus()
        );
    }


    public LoginResponse login(LoginRequest req) {
        var user = users.findByEmail(req.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!encoder.matches(req.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        var roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        var token = jwt.generateToken(user.getId(), user.getEmail(), roleNames);

        audit.record(user.getId(), "LOGIN", "User", user.getId(),
                "{\"email\":\"" + user.getEmail() + "\"}");

        return new LoginResponse(token, user.getId(), user.getEmail(), roleNames);
    }

    public UserResponse me(String email) {
        var user = users.findByEmail(email).orElseThrow();

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()),
                user.getStatus()
        );
    }
}

