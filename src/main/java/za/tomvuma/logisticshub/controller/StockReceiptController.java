package za.tomvuma.logisticshub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.tomvuma.logisticshub.dto.StockReceiptRequest;
import za.tomvuma.logisticshub.dto.StockReceiptResponse;
import za.tomvuma.logisticshub.service.StockReceiptService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/warehouses/receipts")
public class StockReceiptController {

    private final StockReceiptService receiptService;

    public StockReceiptController(StockReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    // RECEIVE stock into warehouse
    @PostMapping
    public ResponseEntity<StockReceiptResponse> receiveStock(@RequestHeader("actorId") Long actorId,
                                                             @Valid @RequestBody StockReceiptRequest req) {
        return ResponseEntity.ok(receiptService.receiveStock(actorId, req));
    }
}
