package za.tomvuma.logisticshub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.tomvuma.logisticshub.entity.InventoryItem;

import java.util.List;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByWarehouseId(Long warehouseId);
    List<InventoryItem> findByProductId(Long productId);
    InventoryItem findByProductIdAndWarehouseId(Long productId, Long warehouseId);
}
