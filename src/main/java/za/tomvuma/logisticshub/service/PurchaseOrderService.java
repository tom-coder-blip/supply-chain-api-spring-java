package za.tomvuma.logisticshub.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.tomvuma.logisticshub.dto.PurchaseOrderResponse;
import za.tomvuma.logisticshub.dto.ShipmentDetailsRequest;
import za.tomvuma.logisticshub.entity.PurchaseOrder;
import za.tomvuma.logisticshub.repository.PurchaseOrderRepository;
import za.tomvuma.logisticshub.repository.ProductRepository;
import za.tomvuma.logisticshub.repository.UserRepository;
import za.tomvuma.logisticshub.repository.WarehouseRepository;
import za.tomvuma.logisticshub.dto.PurchaseOrderRequest;
import za.tomvuma.logisticshub.dto.StockReceiptRequest;
import za.tomvuma.logisticshub.entity.InventoryItem;
import za.tomvuma.logisticshub.entity.Product;
import za.tomvuma.logisticshub.entity.Warehouse;
import za.tomvuma.logisticshub.repository.InventoryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PurchaseOrderService {

    private final PurchaseOrderRepository orderRepo;
    private final AuditService auditService;
    private final WarehouseRepository warehouseRepo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final StockReceiptService receiptService;
    private final InventoryRepository inventoryRepo;

    public PurchaseOrderService(PurchaseOrderRepository orderRepo,
                                AuditService auditService,
                                WarehouseRepository warehouseRepo,
                                UserRepository userRepo,
                                ProductRepository productRepo,
                                StockReceiptService receiptService,
                                InventoryRepository inventoryRepo) {
        this.orderRepo = orderRepo;
        this.auditService = auditService;
        this.warehouseRepo = warehouseRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.receiptService = receiptService;
        this.inventoryRepo = inventoryRepo;
    }

    // CREATE order (warehouse → supplier)
    public PurchaseOrderResponse create(Long actorId, PurchaseOrderRequest req) {
        var warehouse = warehouseRepo.findById(req.warehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        var supplier = userRepo.findById(req.supplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        var product = productRepo.findById(req.productId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        PurchaseOrder order = PurchaseOrder.builder()
                .warehouse(warehouse)
                .supplier(supplier)
                .product(product)
                .quantity(req.quantity())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        PurchaseOrder saved = orderRepo.save(order);

        auditService.record(actorId, "PURCHASE_ORDER_CREATE", "PurchaseOrder", saved.getId(),
                String.format("{\"warehouseId\":%d,\"supplierId\":%d,\"productId\":%d,\"quantity\":%d,\"status\":\"%s\"}",
                        warehouse.getId(), supplier.getId(), product.getId(), req.quantity(), saved.getStatus()));

        return toResponse(saved);
    }

    // VIEW orders for supplier
    public List<PurchaseOrderResponse> getBySupplier(Long supplierId) {
        return orderRepo.findBySupplierId(supplierId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // VIEW orders for warehouse
    public List<PurchaseOrderResponse> getByWarehouse(Long warehouseId) {
        return orderRepo.findByWarehouseId(warehouseId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ACCEPT order (supplier)
    public PurchaseOrderResponse accept(Long actorId, Long orderId) {
        PurchaseOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus("ACCEPTED");
        order.setUpdatedAt(LocalDateTime.now());
        PurchaseOrder saved = orderRepo.save(order);

        auditService.record(actorId, "PURCHASE_ORDER_ACCEPT", "PurchaseOrder", saved.getId(),
                "{\"status\":\"ACCEPTED\"}");

        return toResponse(saved);
    }

    // REJECT order (supplier)
    public PurchaseOrderResponse reject(Long actorId, Long orderId) {
        PurchaseOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus("REJECTED");
        order.setUpdatedAt(LocalDateTime.now());
        PurchaseOrder saved = orderRepo.save(order);

        auditService.record(actorId, "PURCHASE_ORDER_REJECT", "PurchaseOrder", saved.getId(),
                "{\"status\":\"REJECTED\"}");

        return toResponse(saved);
    }

    // CONFIRM quantities (supplier)
    public PurchaseOrderResponse confirm(Long actorId, Long orderId, int confirmedQty) {
        PurchaseOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setQuantity(confirmedQty);
        order.setStatus("CONFIRMED");
        order.setUpdatedAt(LocalDateTime.now());
        PurchaseOrder saved = orderRepo.save(order);

        auditService.record(actorId, "PURCHASE_ORDER_CONFIRM", "PurchaseOrder", saved.getId(),
                String.format("{\"confirmedQty\":%d}", confirmedQty));

        return toResponse(saved);
    }

    public PurchaseOrderResponse ship(Long actorId, ShipmentDetailsRequest req) {
        PurchaseOrder order = orderRepo.findById(req.purchaseOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getStatus().equals("ACCEPTED")) {
            throw new RuntimeException("Only ACCEPTED purchase orders can be shipped");
        }

        // Update order status
        order.setStatus("SHIPPED");
        order.setShipmentDetails(req.shipmentDetails());
        order.setUpdatedAt(LocalDateTime.now());
        PurchaseOrder saved = orderRepo.save(order);

        Product product = order.getProduct();
        Warehouse warehouse = order.getWarehouse();

        // Decrease supplier stock
        if (product.getQuantity() < order.getQuantity()) {
            throw new RuntimeException("Supplier does not have enough stock to ship this order");
        }
        product.setQuantity(product.getQuantity() - order.getQuantity());
        productRepo.save(product);

        // Increase warehouse inventory
        InventoryItem item = inventoryRepo.findByProductIdAndWarehouseId(product.getId(), warehouse.getId());

        if (item == null) {
            item = InventoryItem.builder()
                    .product(product)
                    .warehouse(warehouse)
                    .quantity(order.getQuantity())
                    .build();
        } else {
            item.setQuantity(item.getQuantity() + order.getQuantity());
        }

        inventoryRepo.save(item);

        // Audit log
        auditService.record(actorId, "PURCHASE_ORDER_SHIP", "PurchaseOrder", saved.getId(),
                String.format("{\"status\":\"SHIPPED\",\"shipmentDetails\":\"%s\",\"inventoryIncreased\":%d,\"supplierStockDecreased\":%d}",
                        req.shipmentDetails(), order.getQuantity(), order.getQuantity()));

        return toResponse(saved);
    }



    // CONFIRM goods received (warehouse manager)
    public PurchaseOrderResponse markReceived(Long actorId, Long orderId) {
        PurchaseOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Update order status
        order.setStatus("RECEIVED");
        order.setUpdatedAt(LocalDateTime.now());
        PurchaseOrder saved = orderRepo.save(order);

        // ✅ Trigger stock receipt system
        StockReceiptRequest receiptReq = new StockReceiptRequest(
                order.getProduct().getId(),
                order.getWarehouse().getId(),
                order.getQuantity(),
                "Purchase order received"
        );
        receiptService.receiveStock(actorId, receiptReq);

        // Audit log for order
        auditService.record(actorId, "PURCHASE_ORDER_RECEIVE", "PurchaseOrder", saved.getId(),
                "{\"status\":\"RECEIVED\"}");

        return toResponse(saved);
    }

    // REJECT damaged/incomplete delivery (warehouse manager)
    public PurchaseOrderResponse rejectDelivery(Long actorId, Long orderId, String reason) {
        PurchaseOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Update order status
        order.setStatus("DELIVERY_REJECTED");
        order.setUpdatedAt(LocalDateTime.now());
        PurchaseOrder saved = orderRepo.save(order);

        // Trigger stock receipt system with zero quantity
        StockReceiptRequest receiptReq = new StockReceiptRequest(
                order.getProduct().getId(),
                order.getWarehouse().getId(),
                0, // no stock added
                reason != null ? reason : "Delivery rejected"
        );
        receiptService.receiveStock(actorId, receiptReq);

        // Audit log for order
        auditService.record(actorId, "PURCHASE_ORDER_DELIVERY_REJECT", "PurchaseOrder", saved.getId(),
                String.format("{\"status\":\"DELIVERY_REJECTED\",\"reason\":\"%s\"}", reason));

        return toResponse(saved);
    }


    // Helper: map entity → DTO
    private PurchaseOrderResponse toResponse(PurchaseOrder order) {
        return new PurchaseOrderResponse(
                order.getId(),
                order.getWarehouse().getId(),
                order.getWarehouse().getName(),
                order.getSupplier().getId(),
                order.getSupplier().getUsername(),
                order.getProduct().getId(),
                order.getProduct().getSku(),
                order.getProduct().getName(),
                order.getQuantity(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getShipmentDetails()
        );
    }
}
