package za.tomvuma.logisticshub.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record ShipmentDetailsRequest(
        @NotNull Long purchaseOrderId,
        @NotNull Map<String, Object> shipmentDetails
) {}
