package za.tomvuma.logisticshub.dto;

import jakarta.validation.constraints.NotNull;

public record PurchaseOrderRequest(
        @NotNull Long warehouseId,
        @NotNull Long supplierId,
        @NotNull Long productId,
        @NotNull Integer quantity
) {}
