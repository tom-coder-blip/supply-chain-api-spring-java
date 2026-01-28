package za.tomvuma.logisticshub.dto;

import jakarta.validation.constraints.NotNull;

public record CustomerOrderRequest(
        @NotNull Long productId,
        @NotNull Integer quantity
) {}
