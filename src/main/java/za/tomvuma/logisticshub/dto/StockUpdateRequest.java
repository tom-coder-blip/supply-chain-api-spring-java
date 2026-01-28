package za.tomvuma.logisticshub.dto;

import jakarta.validation.constraints.NotNull;

public record StockUpdateRequest(
        @NotNull Long productId,
        @NotNull Integer newQuantity,
        String reason
) {}
