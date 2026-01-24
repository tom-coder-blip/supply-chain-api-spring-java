package za.tomvuma.logisticshub.controller;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.tomvuma.logisticshub.entity.Role;
import za.tomvuma.logisticshub.repository.RoleRepository;
import za.tomvuma.logisticshub.repository.UserRepository;
import za.tomvuma.logisticshub.service.AuditService;
import za.tomvuma.logisticshub.dto.UserResponse;
import static za.tomvuma.logisticshub.util.RoleConstants.ALLOWED_ROLES;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final UserRepository users;
    private final RoleRepository roles;
    private final AuditService audit;

    public AdminController(UserRepository users, RoleRepository roles, AuditService audit) {
        this.users = users;
        this.roles = roles;
        this.audit = audit;
    }

    @PutMapping("/users/{id}/roles")
    public ResponseEntity<UserResponse> updateUserRoles(
            @PathVariable Long id,
            @RequestBody Set<String> newRoles,
            @RequestHeader("X-Actor-Id") Long adminActorId) {

        var user = users.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        // Only allow valid roles (no ADMIN)
        var validRoles = newRoles.stream()
                .filter(ALLOWED_ROLES::contains)
                .map(roles::findByName)
                .collect(Collectors.toSet());

        user.setRoles(validRoles);
        var saved = users.save(user);

        audit.record(adminActorId, "UPDATE_USER_ROLES", "User", saved.getId(),
                "{\"roles\":\"" + validRoles + "\"}");

        return ResponseEntity.ok(new UserResponse(saved.getId(), saved.getUsername(), saved.getEmail(),
                saved.getRoles().stream().map(Role::getName).collect(Collectors.toSet()),
                saved.getStatus()));
    }
}

