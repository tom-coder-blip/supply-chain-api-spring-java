package za.tomvuma.logisticshub.dto;

public record WarehouseResponse(
        Long id,
        String name,
        String location,
        Long managerId,
        String managerEmail,
        String managerUsername
) {}
