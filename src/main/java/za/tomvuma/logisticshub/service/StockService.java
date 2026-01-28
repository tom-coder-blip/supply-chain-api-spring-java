package za.tomvuma.logisticshub.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.tomvuma.logisticshub.dto.StockUpdateRequest;
import za.tomvuma.logisticshub.dto.StockUpdateResponse;
import za.tomvuma.logisticshub.entity.Product;
import za.tomvuma.logisticshub.entity.StockUpdate;
import za.tomvuma.logisticshub.repository.ProductRepository;
import za.tomvuma.logisticshub.repository.StockUpdateRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
@Transactional
public class StockService {

    private final ProductRepository productRepo;
    private final StockUpdateRepository stockUpdateRepo;
    private final AuditService auditService;

    public StockService(ProductRepository productRepo,
                        StockUpdateRepository stockUpdateRepo,
                        AuditService auditService) {
        this.productRepo = productRepo;
        this.stockUpdateRepo = stockUpdateRepo;
        this.auditService = auditService;
    }

    // UPDATE stock
    public StockUpdateResponse updateStock(Long actorId, StockUpdateRequest req) {
        Product product = productRepo.findById(req.productId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        int oldQty = product.getQuantity();
        product.setQuantity(req.newQuantity());
        productRepo.save(product);

        Map<String, Object> metadata = new HashMap<>();
        if (req.reason() != null) {
            metadata.put("reason", req.reason());
        }

        StockUpdate update = StockUpdate.builder()
                .product(product)
                .oldQuantity(oldQty)
                .newQuantity(req.newQuantity())
                .actorId(actorId)
                .timestamp(LocalDateTime.now())
                .metadata(metadata)
                .build();

        StockUpdate saved = stockUpdateRepo.save(update);

        // Audit log
        auditService.record(actorId,
                "STOCK_UPDATE",
                "Product",
                product.getId(),
                "{\"oldQty\":" + oldQty + ",\"newQty\":" + req.newQuantity() + ",\"reason\":\"" + req.reason() + "\"}");

        return toResponse(saved);
    }

    // NEW: GET stock history for a product
    public List<StockUpdateResponse> getHistory(Long productId) {
        List<StockUpdate> updates = stockUpdateRepo.findByProductId(productId);
        return updates.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Helper: map entity → DTO
    private StockUpdateResponse toResponse(StockUpdate update) {
        return new StockUpdateResponse(
                update.getId(),
                update.getProduct().getId(),
                update.getOldQuantity(),
                update.getNewQuantity(),
                update.getActorId(),
                update.getTimestamp(),
                update.getMetadata()
        );
    }
}
