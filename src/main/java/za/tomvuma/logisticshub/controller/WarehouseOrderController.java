package za.tomvuma.logisticshub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.tomvuma.logisticshub.dto.ProductResponse;
import za.tomvuma.logisticshub.dto.PurchaseOrderRequest;
import za.tomvuma.logisticshub.dto.PurchaseOrderResponse;
import za.tomvuma.logisticshub.service.ProductService;
import za.tomvuma.logisticshub.service.PurchaseOrderService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/warehouses/orders")
public class WarehouseOrderController {

    private final PurchaseOrderService orderService;
    private final ProductService productService;

    public WarehouseOrderController(PurchaseOrderService orderService,
                                    ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }

    // Browse all supplier product catalogs (read-only, warehouse manager)
    @GetMapping("/catalog")
    public ResponseEntity<List<ProductResponse>> browseSupplierCatalog() {
        return ResponseEntity.ok(productService.getAll());
    }

    // Browse products by supplier
    @GetMapping("/catalog/supplier/{supplierId}")
    public ResponseEntity<List<ProductResponse>> browseBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(productService.getBySupplierId(supplierId));
    }

    // Browse products by category
    @GetMapping("/catalog/category/{category}")
    public ResponseEntity<List<ProductResponse>> browseByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.getByCategory(category));
    }

    // Create purchase order to supplier
    @PostMapping
    public ResponseEntity<PurchaseOrderResponse> create(@RequestHeader("actorId") Long actorId,
                                                        @Valid @RequestBody PurchaseOrderRequest req) {
        return ResponseEntity.ok(orderService.create(actorId, req));
    }

    // View orders for warehouse
    @GetMapping("/{warehouseId}")
    public ResponseEntity<List<PurchaseOrderResponse>> getByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(orderService.getByWarehouse(warehouseId));
    }

    // Confirm goods received
    @PutMapping("/{orderId}/receive")
    public ResponseEntity<PurchaseOrderResponse> confirmReceived(@RequestHeader("actorId") Long actorId,
                                                                 @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.markReceived(actorId, orderId));
    }

    // Reject damaged/incomplete delivery
    @PutMapping("/{orderId}/reject-delivery")
    public ResponseEntity<PurchaseOrderResponse> rejectDelivery(@RequestHeader("actorId") Long actorId,
                                                                @PathVariable Long orderId,
                                                                @RequestParam String reason) {
        return ResponseEntity.ok(orderService.rejectDelivery(actorId, orderId, reason));
    }
}
