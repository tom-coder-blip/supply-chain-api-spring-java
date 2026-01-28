package za.tomvuma.logisticshub.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.tomvuma.logisticshub.dto.ProductRequest;
import za.tomvuma.logisticshub.dto.ProductResponse;
import za.tomvuma.logisticshub.entity.Product;
import za.tomvuma.logisticshub.entity.User;
import za.tomvuma.logisticshub.repository.ProductRepository;
import za.tomvuma.logisticshub.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final AuditService auditService;

    public ProductService(ProductRepository productRepo, UserRepository userRepo, AuditService auditService) {
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.auditService = auditService;
    }

    // READ (by supplier, warehouse manager browsing)
    public List<ProductResponse> getBySupplierId(Long supplierId) {
        return productRepo.findBySupplierId(supplierId).stream()
                .filter(p -> !p.isArchived())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // READ (by category, warehouse manager browsing)
    public List<ProductResponse> getByCategory(String category) {
        return productRepo.findByCategoryIgnoreCase(category).stream()
                .filter(p -> !p.isArchived())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // CREATE
    public ProductResponse create(Long actorId, ProductRequest req) {
        if (productRepo.existsBySku(req.sku())) {
            throw new RuntimeException("Product with SKU already exists");
        }

        User supplier = userRepo.findById(req.supplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        Product product = Product.builder()
                .sku(req.sku())
                .name(req.name())
                .description(req.description())   // NEW
                .unit(req.unit())
                .price(req.price())               // NEW
                .quantity(req.quantity())
                .supplier(supplier)
                .category(req.category())
                .archived(false)                  // NEW default
                .build();

        Product saved = productRepo.save(product);

        // Audit log
        auditService.record(actorId,
                "PRODUCT_CREATE",
                "Product",
                saved.getId(),
                String.format("{\"sku\":\"%s\",\"name\":\"%s\",\"description\":\"%s\",\"unit\":\"%s\",\"price\":%.2f,\"quantity\":%d,\"supplierId\":%d,\"category\":\"%s\"}",
                        saved.getSku(), saved.getName(), saved.getDescription(), saved.getUnit(),
                        saved.getPrice(), saved.getQuantity(), saved.getSupplier().getId(),
                        saved.getCategory())
        );


        return toResponse(saved);
    }

    // READ (single)
    public ProductResponse getById(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return toResponse(product);
    }

    // READ (all) → only active products
    public List<ProductResponse> getAll() {
        return productRepo.findAll().stream()
                .filter(p -> !p.isArchived()) // NEW: exclude archived
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // READ (supplier-only)
    public List<ProductResponse> getBySupplier(Long supplierId) {
        return productRepo.findAll().stream()
                .filter(p -> p.getSupplier().getId().equals(supplierId) && !p.isArchived())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // UPDATE
    public ProductResponse update(Long actorId, Long id, ProductRequest req) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        User supplier = userRepo.findById(req.supplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        String oldSku = product.getSku();
        String oldName = product.getName();
        String oldUnit = product.getUnit();
        int oldQty = product.getQuantity();
        double oldPrice = product.getPrice();          // NEW
        String oldDescription = product.getDescription(); // NEW
        Long oldSupplierId = product.getSupplier().getId();
        String oldCategory = product.getCategory(); // NEW

        product.setSku(req.sku());
        product.setName(req.name());
        product.setDescription(req.description());     // NEW
        product.setUnit(req.unit());
        product.setPrice(req.price());                 // NEW
        product.setQuantity(req.quantity());
        product.setSupplier(supplier);
        product.setCategory(req.category());// NEW

        product.setCategory(req.category());

        Product updated = productRepo.save(product);

// Audit log
        auditService.record(actorId,
                "PRODUCT_UPDATE",
                "Product",
                updated.getId(),
                String.format("{\"oldSku\":\"%s\",\"newSku\":\"%s\",\"oldName\":\"%s\",\"newName\":\"%s\",\"oldDescription\":\"%s\",\"newDescription\":\"%s\",\"oldUnit\":\"%s\",\"newUnit\":\"%s\",\"oldPrice\":%.2f,\"newPrice\":%.2f,\"oldQty\":%d,\"newQty\":%d,\"oldSupplierId\":%d,\"newSupplierId\":%d,\"oldCategory\":\"%s\",\"newCategory\":\"%s\"}",
                        oldSku, updated.getSku(),
                        oldName, updated.getName(),
                        oldDescription, updated.getDescription(),
                        oldUnit, updated.getUnit(),
                        oldPrice, updated.getPrice(),
                        oldQty, updated.getQuantity(),
                        oldSupplierId, updated.getSupplier().getId(),
                        oldCategory, updated.getCategory())
        );


        return toResponse(updated);
    }

    // DELETE → soft delete
    public void delete(Long actorId, Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setArchived(true); // NEW: soft-delete
        productRepo.save(product);

        // Audit log
        auditService.record(actorId,
                "PRODUCT_ARCHIVE",
                "Product",
                product.getId(),
                String.format("{\"sku\":\"%s\",\"name\":\"%s\",\"archived\":true}", product.getSku(), product.getName())
        );
    }

    // Helper: map entity → DTO
    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),   // NEW
                product.getUnit(),
                product.getPrice(),         // NEW
                product.getQuantity(),
                product.isArchived(),       // NEW
                product.getSupplier().getId(),
                product.getSupplier().getEmail(),
                product.getSupplier().getUsername(),
                product.getCategory()
        );
    }
}
