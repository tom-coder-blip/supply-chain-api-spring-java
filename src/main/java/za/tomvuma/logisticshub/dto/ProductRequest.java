package za.tomvuma.logisticshub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductRequest(
        @NotBlank @Size(min = 3, max = 50) String sku,
        @NotBlank @Size(min = 3, max = 100) String name,
        String description,
        String unit,
        @NotNull Double price,
        @NotNull Integer quantity,
        @NotNull Long supplierId,
        String category   //  NEW
) {}
