package za.tomvuma.logisticshub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.tomvuma.logisticshub.entity.Product;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
    boolean existsBySku(String sku);
    // Find products by supplier
    List<Product> findBySupplierId(Long supplierId);
    // Find products by category
    List<Product> findByCategoryIgnoreCase(String category);
}
