package za.tomvuma.logisticshub.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record StockReceiptResponse(
        Long id,
        Long productId,
        String productSku,
        String productName,
        Long warehouseId,
        String warehouseName,
        int quantityReceived,
        Long actorId,
        LocalDateTime timestamp,
        Map<String, Object> metadata
) {}
