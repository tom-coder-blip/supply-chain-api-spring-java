package za.tomvuma.logisticshub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.tomvuma.logisticshub.entity.StockUpdate;

import java.util.List;

public interface StockUpdateRepository extends JpaRepository<StockUpdate, Long> {
    List<StockUpdate> findByProductId(Long productId);
}
