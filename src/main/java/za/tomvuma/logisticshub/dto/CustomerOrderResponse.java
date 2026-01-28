package za.tomvuma.logisticshub.dto;

import java.time.LocalDateTime;

public record CustomerOrderResponse(
        Long id,
        Long customerId,
        String customerUsername,
        Long productId,
        String productSku,
        String productName,
        int quantity,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String shipmentDetails
) {}
