package za.tomvuma.logisticshub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.tomvuma.logisticshub.dto.StockUpdateRequest;
import za.tomvuma.logisticshub.dto.StockUpdateResponse;
import za.tomvuma.logisticshub.service.StockService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/suppliers/stock")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/update")
    public ResponseEntity<StockUpdateResponse> updateStock(@RequestHeader("actorId") Long actorId,
                                                           @Valid @RequestBody StockUpdateRequest req) {
        return ResponseEntity.ok(stockService.updateStock(actorId, req));
    }
}
