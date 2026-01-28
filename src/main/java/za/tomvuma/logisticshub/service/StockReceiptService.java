package za.tomvuma.logisticshub.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.tomvuma.logisticshub.dto.StockReceiptRequest;
import za.tomvuma.logisticshub.dto.StockReceiptResponse;
import za.tomvuma.logisticshub.entity.InventoryItem;
import za.tomvuma.logisticshub.entity.Product;
import za.tomvuma.logisticshub.entity.StockReceipt;
import za.tomvuma.logisticshub.entity.Warehouse;
import za.tomvuma.logisticshub.repository.InventoryRepository;
import za.tomvuma.logisticshub.repository.ProductRepository;
import za.tomvuma.logisticshub.repository.StockReceiptRepository;
import za.tomvuma.logisticshub.repository.WarehouseRepository;
import java.util.HashMap;
import java.util.Map;

import java.time.LocalDateTime;

@Service
@Transactional
public class StockReceiptService {

    private final ProductRepository productRepo;
    private final WarehouseRepository warehouseRepo;
    private final InventoryRepository inventoryRepo;
    private final StockReceiptRepository receiptRepo;
    private final AuditService auditService;

    public StockReceiptService(ProductRepository productRepo,
                               WarehouseRepository warehouseRepo,
                               InventoryRepository inventoryRepo,
                               StockReceiptRepository receiptRepo,
                               AuditService auditService) {
        this.productRepo = productRepo;
        this.warehouseRepo = warehouseRepo;
        this.inventoryRepo = inventoryRepo;
        this.receiptRepo = receiptRepo;
        this.auditService = auditService;
    }

    public StockReceiptResponse receiveStock(Long actorId, StockReceiptRequest req) {
        Product product = productRepo.findById(req.productId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Warehouse warehouse = warehouseRepo.findById(req.warehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        // Update inventory
        InventoryItem item = inventoryRepo.findByWarehouseId(warehouse.getId()).stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(InventoryItem.builder()
                        .product(product)
                        .warehouse(warehouse)
                        .quantity(0)
                        .build());

        int oldQty = item.getQuantity();
        item.setQuantity(oldQty + req.quantityReceived());
        inventoryRepo.save(item);

        Map<String, Object> metadata = new HashMap<>();
        if (req.reason() != null) {
            metadata.put("reason", req.reason());
        }

        StockReceipt receipt = StockReceipt.builder()
                .product(product)
                .warehouse(warehouse)
                .quantityReceived(req.quantityReceived())
                .actorId(actorId)
                .timestamp(LocalDateTime.now())
                .metadata(metadata)
                .build();


        StockReceipt saved = receiptRepo.save(receipt);

        // Audit log
        auditService.record(actorId,
                "STOCK_RECEIVED",
                "Warehouse",
                warehouse.getId(),
                "{\"product\":\"" + product.getName() + "\",\"received\":" + req.quantityReceived() + ",\"reason\":\"" + req.reason() + "\"}");

        return toResponse(saved);
    }

    private StockReceiptResponse toResponse(StockReceipt receipt) {
        return new StockReceiptResponse(
                receipt.getId(),
                receipt.getProduct().getId(),
                receipt.getProduct().getSku(),
                receipt.getProduct().getName(),
                receipt.getWarehouse().getId(),
                receipt.getWarehouse().getName(),
                receipt.getQuantityReceived(),
                receipt.getActorId(),
                receipt.getTimestamp(),
                receipt.getMetadata()
        );
    }
}
