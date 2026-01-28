package za.tomvuma.logisticshub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record WarehouseRequest(
        @NotBlank @Size(min = 3, max = 100) String name,
        String location,
        @NotNull Long managerId
) {}

