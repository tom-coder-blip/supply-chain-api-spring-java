package za.tomvuma.logisticshub.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.tomvuma.logisticshub.dto.CustomerOrderRequest;
import za.tomvuma.logisticshub.dto.CustomerOrderResponse;
import za.tomvuma.logisticshub.entity.CustomerOrder;
import za.tomvuma.logisticshub.entity.Product;
import za.tomvuma.logisticshub.entity.User;
import za.tomvuma.logisticshub.repository.CustomerOrderRepository;
import za.tomvuma.logisticshub.repository.ProductRepository;
import za.tomvuma.logisticshub.repository.UserRepository;
import za.tomvuma.logisticshub.entity.InventoryItem;
import za.tomvuma.logisticshub.repository.InventoryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerOrderService {

    private final CustomerOrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final AuditService auditService;
    private final InventoryRepository inventoryRepo;

    public CustomerOrderService(CustomerOrderRepository orderRepo,
                                ProductRepository productRepo,
                                UserRepository userRepo,
                                AuditService auditService,
                                InventoryRepository inventoryRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.auditService = auditService;
        this.inventoryRepo = inventoryRepo;
    }

    // CREATE order
    public CustomerOrderResponse create(Long actorId, CustomerOrderRequest req) {
        User customer = userRepo.findById(actorId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Product product = productRepo.findById(req.productId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.isArchived()) {
            throw new RuntimeException("Product is not available");
        }

        CustomerOrder order = CustomerOrder.builder()
                .customer(customer)
                .product(product)
                .quantity(req.quantity())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CustomerOrder saved = orderRepo.save(order);

        // Audit log
        auditService.record(actorId, "CUSTOMER_ORDER_CREATE", "CustomerOrder", saved.getId(),
                String.format("{\"productId\":%d,\"quantity\":%d,\"status\":\"%s\"}",
                        product.getId(), req.quantity(), saved.getStatus()));

        return toResponse(saved);
    }

    // VIEW single order
    public CustomerOrderResponse getById(Long orderId) {
        CustomerOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return toResponse(order);
    }

    // VIEW all orders for customer
    public List<CustomerOrderResponse> getByCustomer(Long customerId) {
        return orderRepo.findByCustomerId(customerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // CANCEL order (only if not fulfilled)
    public CustomerOrderResponse cancel(Long actorId, Long orderId) {
        CustomerOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getStatus().equals("PENDING") && !order.getStatus().equals("ACCEPTED")) {
            throw new RuntimeException("Order cannot be cancelled at this stage");
        }

        order.setStatus("CANCELLED");
        order.setUpdatedAt(LocalDateTime.now());
        CustomerOrder saved = orderRepo.save(order);

        // Audit log
        auditService.record(actorId, "CUSTOMER_ORDER_CANCEL", "CustomerOrder", saved.getId(),
                "{\"status\":\"CANCELLED\"}");

        return toResponse(saved);
    }

    // VIEW all customer orders (for warehouse managers)
    public List<CustomerOrderResponse> getAll() {
        return orderRepo.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ACCEPT order
    public CustomerOrderResponse accept(Long actorId, Long orderId) {
        CustomerOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getStatus().equals("PENDING")) {
            throw new RuntimeException("Only PENDING orders can be accepted");
        }

        order.setStatus("ACCEPTED");
        order.setUpdatedAt(LocalDateTime.now());
        CustomerOrder saved = orderRepo.save(order);

        auditService.record(actorId, "CUSTOMER_ORDER_ACCEPT", "CustomerOrder", saved.getId(),
                "{\"status\":\"ACCEPTED\"}");

        return toResponse(saved);
    }


    // REJECT order
    public CustomerOrderResponse reject(Long actorId, Long orderId, String reason) {
        CustomerOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getStatus().equals("PENDING")) {
            throw new RuntimeException("Only PENDING orders can be rejected");
        }

        order.setStatus("REJECTED");
        order.setUpdatedAt(LocalDateTime.now());
        CustomerOrder saved = orderRepo.save(order);

        auditService.record(actorId, "CUSTOMER_ORDER_REJECT", "CustomerOrder", saved.getId(),
                String.format("{\"status\":\"REJECTED\",\"reason\":\"%s\"}", reason));

        return toResponse(saved);
    }

    // SHIP order
    public CustomerOrderResponse ship(Long actorId, Long orderId, String shipmentDetails) {
        CustomerOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getStatus().equals("ACCEPTED")) {
            throw new RuntimeException("Only ACCEPTED orders can be shipped");
        }

        // Deduct inventory
        List<InventoryItem> items = inventoryRepo.findByProductId(order.getProduct().getId());
        if (items.isEmpty()) {
            throw new RuntimeException("No inventory found for product");
        }

        // For simplicity: deduct from the first warehouse that has stock
        InventoryItem item = items.stream()
                .filter(i -> i.getQuantity() >= order.getQuantity())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Insufficient stock to fulfill order"));

        item.setQuantity(item.getQuantity() - order.getQuantity());
        inventoryRepo.save(item);

        // Update order status
        order.setStatus("SHIPPED");
        order.setShipmentDetails(shipmentDetails);
        order.setUpdatedAt(LocalDateTime.now());
        CustomerOrder saved = orderRepo.save(order);

        // Audit log
        auditService.record(actorId, "CUSTOMER_ORDER_SHIP", "CustomerOrder", saved.getId(),
                String.format("{\"status\":\"SHIPPED\",\"shipmentDetails\":\"%s\",\"inventoryDeducted\":%d}",
                        shipmentDetails, order.getQuantity()));

        return toResponse(saved);
    }


    // CONFIRM order received (customer action)
    public CustomerOrderResponse confirmReceived(Long actorId, Long orderId) {
        CustomerOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getStatus().equals("SHIPPED")) {
            throw new RuntimeException("Only SHIPPED orders can be confirmed as received");
        }

        order.setStatus("RECEIVED");
        order.setUpdatedAt(LocalDateTime.now());
        CustomerOrder saved = orderRepo.save(order);

        auditService.record(actorId, "CUSTOMER_ORDER_RECEIVED", "CustomerOrder", saved.getId(),
                "{\"status\":\"RECEIVED\"}");

        return toResponse(saved);
    }

    // Helper: map entity → DTO
    private CustomerOrderResponse toResponse(CustomerOrder order) {
        return new CustomerOrderResponse(
                order.getId(),
                order.getCustomer().getId(),
                order.getCustomer().getUsername(),
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
