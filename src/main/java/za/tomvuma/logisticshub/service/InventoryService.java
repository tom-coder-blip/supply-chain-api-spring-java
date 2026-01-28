package za.tomvuma.logisticshub.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.tomvuma.logisticshub.dto.InventoryResponse;
import za.tomvuma.logisticshub.entity.InventoryItem;
import za.tomvuma.logisticshub.repository.InventoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepo;

    public InventoryService(InventoryRepository inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    // VIEW inventory for a warehouse
    public List<InventoryResponse> getByWarehouse(Long warehouseId) {
        return inventoryRepo.findByWarehouseId(warehouseId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // VIEW inventory for a product across warehouses
    public List<InventoryResponse> getByProduct(Long productId) {
        return inventoryRepo.findByProductId(productId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Low stock check
    public List<InventoryResponse> getLowStock(Long warehouseId, int threshold) {
        return inventoryRepo.findByWarehouseId(warehouseId).stream()
                .filter(item -> item.getQuantity() < threshold)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private InventoryResponse toResponse(InventoryItem item) {
        return new InventoryResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getSku(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getWarehouse().getId(),
                item.getWarehouse().getName()
        );
    }
}
