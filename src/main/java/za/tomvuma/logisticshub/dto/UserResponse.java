package za.tomvuma.logisticshub.dto;

import java.util.Set;

public record UserResponse(
        Long id,
        String username,
        String email,
        Set<String> roles,
        String status
) {}

