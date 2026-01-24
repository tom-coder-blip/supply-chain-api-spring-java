package za.tomvuma.logisticshub.dto;

import java.util.Set;

public record LoginResponse(String token, Long userId, String email, Set<String> roles) {}


