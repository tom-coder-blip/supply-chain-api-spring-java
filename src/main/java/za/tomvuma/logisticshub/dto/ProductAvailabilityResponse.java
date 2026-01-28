package za.tomvuma.logisticshub.dto;

import java.util.List;

public record ProductAvailabilityResponse(
        Long productId,
        String sku,
        String name,
        int totalAvailable,
        List<WarehouseStock> warehouseStocks
) {
    public static record WarehouseStock(
            Long warehouseId,
            String warehouseName,
            int quantity
    ) {}
}
