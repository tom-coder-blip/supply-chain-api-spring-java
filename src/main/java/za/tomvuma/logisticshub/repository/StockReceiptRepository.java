package za.tomvuma.logisticshub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.tomvuma.logisticshub.entity.StockReceipt;

import java.util.List;

public interface StockReceiptRepository extends JpaRepository<StockReceipt, Long> {
    List<StockReceipt> findByWarehouseId(Long warehouseId);
    List<StockReceipt> findByProductId(Long productId);
}
