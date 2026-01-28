package za.tomvuma.logisticshub.dto;

public record InventoryResponse(
        Long id,
        Long productId,
        String productSku,
        String productName,
        int quantity,
        Long warehouseId,
        String warehouseName
) {}
