package za.tomvuma.logisticshub.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record PurchaseOrderResponse(
        Long id,
        Long warehouseId,
        String warehouseName,
        Long supplierId,
        String supplierName,
        Long productId,
        String productSku,
        String productName,
        int quantity,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Map<String, Object> shipmentDetails
) {}
