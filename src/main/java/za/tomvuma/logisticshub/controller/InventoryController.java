package za.tomvuma.logisticshub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.tomvuma.logisticshub.dto.InventoryResponse;
import za.tomvuma.logisticshub.service.InventoryService;

import java.util.List;

@RestController
@RequestMapping("/warehouses/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // VIEW inventory for a warehouse
    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<InventoryResponse>> getByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(inventoryService.getByWarehouse(warehouseId));
    }

    // VIEW inventory for a product across warehouses
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<InventoryResponse>> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getByProduct(productId));
    }

    // VIEW low stock items in a warehouse
    @GetMapping("/warehouse/{warehouseId}/low-stock")
    public ResponseEntity<List<InventoryResponse>> getLowStock(@PathVariable Long warehouseId,
                                                               @RequestParam(defaultValue = "10") int threshold) {
        return ResponseEntity.ok(inventoryService.getLowStock(warehouseId, threshold));
    }
}
