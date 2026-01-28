package za.tomvuma.logisticshub.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.tomvuma.logisticshub.dto.WarehouseRequest;
import za.tomvuma.logisticshub.dto.WarehouseResponse;
import za.tomvuma.logisticshub.entity.User;
import za.tomvuma.logisticshub.entity.Warehouse;
import za.tomvuma.logisticshub.repository.UserRepository;
import za.tomvuma.logisticshub.repository.WarehouseRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WarehouseService {

    private final WarehouseRepository warehouseRepo;
    private final UserRepository userRepo;

    public WarehouseService(WarehouseRepository warehouseRepo, UserRepository userRepo) {
        this.warehouseRepo = warehouseRepo;
        this.userRepo = userRepo;
    }

    // CREATE
    public WarehouseResponse create(WarehouseRequest req) {
        if (warehouseRepo.existsByName(req.name())) {
            throw new RuntimeException("Warehouse with this name already exists");
        }

        User manager = userRepo.findById(req.managerId())
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        Warehouse warehouse = Warehouse.builder()
                .name(req.name())
                .location(req.location())
                .manager(manager)
                .build();

        Warehouse saved = warehouseRepo.save(warehouse);
        return toResponse(saved);
    }

    // VIEW single
    public WarehouseResponse getById(Long id) {
        Warehouse warehouse = warehouseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        return toResponse(warehouse);
    }

    // VIEW all
    public List<WarehouseResponse> getAll() {
        return warehouseRepo.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private WarehouseResponse toResponse(Warehouse warehouse) {
        return new WarehouseResponse(
                warehouse.getId(),
                warehouse.getName(),
                warehouse.getLocation(),
                warehouse.getManager().getId(),
                warehouse.getManager().getEmail(),
                warehouse.getManager().getUsername()
        );
    }
}
