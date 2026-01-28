package za.tomvuma.logisticshub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.tomvuma.logisticshub.dto.ProductRequest;
import za.tomvuma.logisticshub.dto.ProductResponse;
import za.tomvuma.logisticshub.dto.StockUpdateResponse;
import za.tomvuma.logisticshub.service.ProductService;
import za.tomvuma.logisticshub.service.StockService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/suppliers/products")
public class SupplierController {

    private final ProductService productService;
    private final StockService stockService;

    public SupplierController(ProductService productService, StockService stockService) {
        this.productService = productService;
        this.stockService = stockService;
    }

    // CREATE product
    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestHeader("actorId") Long actorId,
                                                  @Valid @RequestBody ProductRequest req) {
        return ResponseEntity.ok(productService.create(actorId, req));
    }

    // READ single product
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    // READ all active products (system-wide)
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(productService.getAll());
    }

    // READ supplier's own products only
    @GetMapping("/mine/{supplierId}")
    public ResponseEntity<List<ProductResponse>> getBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(productService.getBySupplier(supplierId));
    }

    // UPDATE product
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@RequestHeader("actorId") Long actorId,
                                                  @PathVariable Long id,
                                                  @Valid @RequestBody ProductRequest req) {
        return ResponseEntity.ok(productService.update(actorId, id, req));
    }

    // ARCHIVE product (soft-delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> archive(@RequestHeader("actorId") Long actorId,
                                        @PathVariable Long id) {
        productService.delete(actorId, id); // now soft-delete
        return ResponseEntity.noContent().build();
    }

    // VIEW stock movement history for a product
    @GetMapping("/{productId}/stock-history")
    public ResponseEntity<List<StockUpdateResponse>> getStockHistory(@PathVariable Long productId) {
        return ResponseEntity.ok(stockService.getHistory(productId));
    }
}
