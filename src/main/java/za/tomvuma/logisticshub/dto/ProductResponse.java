package za.tomvuma.logisticshub.dto;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        String unit,
        double price,
        int quantity,
        boolean archived,
        Long supplierId,
        String supplierEmail,
        String supplierUsername,
        String category   //  NEW
) {}

