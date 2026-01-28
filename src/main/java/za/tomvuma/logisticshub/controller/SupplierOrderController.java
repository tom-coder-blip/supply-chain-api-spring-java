package za.tomvuma.logisticshub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.tomvuma.logisticshub.dto.PurchaseOrderResponse;
import za.tomvuma.logisticshub.dto.ShipmentDetailsRequest;
import za.tomvuma.logisticshub.service.PurchaseOrderService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/suppliers/orders")
public class SupplierOrderController {

    private final PurchaseOrderService orderService;

    public SupplierOrderController(PurchaseOrderService orderService) {
        this.orderService = orderService;
    }

    // VIEW orders for supplier
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<PurchaseOrderResponse>> getBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(orderService.getBySupplier(supplierId));
    }

    // VIEW orders for warehouse (optional, could move later)
    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<PurchaseOrderResponse>> getByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(orderService.getByWarehouse(warehouseId));
    }

    // ACCEPT order
    @PutMapping("/{orderId}/accept")
    public ResponseEntity<PurchaseOrderResponse> accept(@RequestHeader("actorId") Long actorId,
                                                        @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.accept(actorId, orderId));
    }

    // REJECT order
    @PutMapping("/{orderId}/reject")
    public ResponseEntity<PurchaseOrderResponse> reject(@RequestHeader("actorId") Long actorId,
                                                        @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.reject(actorId, orderId));
    }

    // CONFIRM quantities
    @PutMapping("/{orderId}/confirm")
    public ResponseEntity<PurchaseOrderResponse> confirm(@RequestHeader("actorId") Long actorId,
                                                         @PathVariable Long orderId,
                                                         @RequestParam int confirmedQty) {
        return ResponseEntity.ok(orderService.confirm(actorId, orderId, confirmedQty));
    }

    // MARK as SHIPPED + upload shipment details
    @PutMapping("/{orderId}/ship")
    public ResponseEntity<PurchaseOrderResponse> ship(@RequestHeader("actorId") Long actorId,
                                                      @Valid @RequestBody ShipmentDetailsRequest req) {
        return ResponseEntity.ok(orderService.ship(actorId, req));
    }
}
