package za.tomvuma.logisticshub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record LoginRequest(@Email @NotBlank String email,
                           @NotBlank @Size(min = 8) String password) {}

