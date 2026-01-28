package za.tomvuma.logisticshub.dto;

import jakarta.validation.constraints.NotNull;

public record StockReceiptRequest(
        @NotNull Long productId,
        @NotNull Long warehouseId,
        @NotNull Integer quantityReceived,
        String reason
) {}
