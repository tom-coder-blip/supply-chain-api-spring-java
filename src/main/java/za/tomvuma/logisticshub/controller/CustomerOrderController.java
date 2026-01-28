package za.tomvuma.logisticshub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.tomvuma.logisticshub.dto.CustomerOrderRequest;
import za.tomvuma.logisticshub.dto.CustomerOrderResponse;
import za.tomvuma.logisticshub.service.CustomerOrderService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/customers/orders")
public class CustomerOrderController {

    private final CustomerOrderService orderService;

    public CustomerOrderController(CustomerOrderService orderService) {
        this.orderService = orderService;
    }

    // Place a new order
    @PostMapping
    public ResponseEntity<CustomerOrderResponse> create(@RequestHeader("actorId") Long actorId,
                                                        @Valid @RequestBody CustomerOrderRequest req) {
        return ResponseEntity.ok(orderService.create(actorId, req));
    }

    // View a single order
    @GetMapping("/{orderId}")
    public ResponseEntity<CustomerOrderResponse> getById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getById(orderId));
    }

    // View all orders for a customer
    @GetMapping
    public ResponseEntity<List<CustomerOrderResponse>> getByCustomer(@RequestHeader("actorId") Long customerId) {
        return ResponseEntity.ok(orderService.getByCustomer(customerId));
    }

    // Cancel an order (only if not fulfilled)
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<CustomerOrderResponse> cancel(@RequestHeader("actorId") Long actorId,
                                                        @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancel(actorId, orderId));
    }

    // Confirm order received (customer marks delivery complete)
    @PutMapping("/{orderId}/confirm-received")
    public ResponseEntity<CustomerOrderResponse> confirmReceived(@RequestHeader("actorId") Long actorId,
                                                                 @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.confirmReceived(actorId, orderId));
    }
}
