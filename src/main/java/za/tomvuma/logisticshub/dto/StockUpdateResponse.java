package za.tomvuma.logisticshub.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record StockUpdateResponse(
        Long id,
        Long productId,
        int oldQuantity,
        int newQuantity,
        Long actorId,
        LocalDateTime timestamp,
        Map<String, Object> metadata
) {}
