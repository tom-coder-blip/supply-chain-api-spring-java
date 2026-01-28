package za.tomvuma.logisticshub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.tomvuma.logisticshub.dto.CustomerOrderResponse;
import za.tomvuma.logisticshub.service.CustomerOrderService;

import java.util.List;

@RestController
@RequestMapping("/warehouses/customer-orders")
public class WarehouseCustomerOrderController {

    private final CustomerOrderService orderService;

    public WarehouseCustomerOrderController(CustomerOrderService orderService) {
        this.orderService = orderService;
    }

    // View all customer orders (for warehouse managers)
    @GetMapping
    public ResponseEntity<List<CustomerOrderResponse>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    // Accept a customer order
    @PutMapping("/{orderId}/accept")
    public ResponseEntity<CustomerOrderResponse> accept(@RequestHeader("actorId") Long actorId,
                                                        @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.accept(actorId, orderId));
    }

    // Reject a customer order
    @PutMapping("/{orderId}/reject")
    public ResponseEntity<CustomerOrderResponse> reject(@RequestHeader("actorId") Long actorId,
                                                        @PathVariable Long orderId,
                                                        @RequestParam String reason) {
        return ResponseEntity.ok(orderService.reject(actorId, orderId, reason));
    }

    // Mark order as shipped
    @PutMapping("/{orderId}/ship")
    public ResponseEntity<CustomerOrderResponse> ship(@RequestHeader("actorId") Long actorId,
                                                      @PathVariable Long orderId,
                                                      @RequestParam String shipmentDetails) {
        return ResponseEntity.ok(orderService.ship(actorId, orderId, shipmentDetails));
    }
}
