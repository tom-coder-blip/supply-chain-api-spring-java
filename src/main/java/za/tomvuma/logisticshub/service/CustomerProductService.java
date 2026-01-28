package za.tomvuma.logisticshub.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.tomvuma.logisticshub.dto.ProductResponse;
import za.tomvuma.logisticshub.dto.ProductAvailabilityResponse;
import za.tomvuma.logisticshub.entity.Product;
import za.tomvuma.logisticshub.entity.InventoryItem;
import za.tomvuma.logisticshub.repository.ProductRepository;
import za.tomvuma.logisticshub.repository.InventoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerProductService {

    private final ProductRepository productRepo;
    private final InventoryRepository inventoryRepo;

    public CustomerProductService(ProductRepository productRepo,
                                  InventoryRepository inventoryRepo) {
        this.productRepo = productRepo;
        this.inventoryRepo = inventoryRepo;
    }

    // Browse all active products
    public List<ProductResponse> getAll() {
        return productRepo.findAll().stream()
                .filter(p -> !p.isArchived())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // View product details
    public ProductResponse getById(Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.isArchived()) {
            throw new RuntimeException("Product is not available");
        }
        return toResponse(product);
    }

    // Search + filter products
    public List<ProductResponse> search(String query, String category) {
        return productRepo.findAll().stream()
                .filter(p -> !p.isArchived())
                .filter(p -> query == null || p.getName().toLowerCase().contains(query.toLowerCase()))
                .filter(p -> category == null || p.getCategory().equalsIgnoreCase(category))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // View stock availability (read-only)
    public ProductAvailabilityResponse getAvailability(Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<InventoryItem> items = inventoryRepo.findByProductId(productId);

        int totalAvailable = items.stream()
                .mapToInt(InventoryItem::getQuantity)
                .sum();

        return new ProductAvailabilityResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                totalAvailable,
                items.stream()
                        .map(i -> new ProductAvailabilityResponse.WarehouseStock(
                                i.getWarehouse().getId(),
                                i.getWarehouse().getName(),
                                i.getQuantity()
                        ))
                        .collect(Collectors.toList())
        );
    }

    // Helper: map entity → DTO
    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getUnit(),
                product.getPrice(),
                product.getQuantity(),
                product.isArchived(),
                product.getSupplier().getId(),
                product.getSupplier().getEmail(),
                product.getSupplier().getUsername(),
                product.getCategory()
        );
    }
}
