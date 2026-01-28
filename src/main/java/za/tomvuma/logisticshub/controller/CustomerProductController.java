package za.tomvuma.logisticshub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.tomvuma.logisticshub.dto.ProductResponse;
import za.tomvuma.logisticshub.dto.ProductAvailabilityResponse;
import za.tomvuma.logisticshub.service.CustomerProductService;

import java.util.List;

@RestController
@RequestMapping("/customers/products")
public class CustomerProductController {

    private final CustomerProductService productService;

    public CustomerProductController(CustomerProductService productService) {
        this.productService = productService;
    }

    // Browse all active products
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(productService.getAll());
    }

    // View product details
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getById(productId));
    }

    // Search + filter products
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> search(@RequestParam(required = false) String query,
                                                        @RequestParam(required = false) String category) {
        return ResponseEntity.ok(productService.search(query, category));
    }

    // View stock availability (read-only)
    @GetMapping("/{productId}/availability")
    public ResponseEntity<ProductAvailabilityResponse> getAvailability(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getAvailability(productId));
    }
}
