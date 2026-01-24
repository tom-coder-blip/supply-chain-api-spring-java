package za.tomvuma.logisticshub.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import za.tomvuma.logisticshub.entity.AuditLog;
import za.tomvuma.logisticshub.repository.AuditLogRepository;

import java.time.LocalDateTime;

@Service
public class AuditService {

    private final AuditLogRepository repo;
    private final ObjectMapper mapper;

    public AuditService(AuditLogRepository repo, ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public void record(Long actorId,
                       String action,
                       String entityType,
                       Long entityId,
                       Object metadata) {

        try {
            JsonNode json = mapper.valueToTree(metadata); // convert to JsonNode

            var log = AuditLog.builder()
                    .actorId(actorId)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .metadata(json) // jsonb-safe
                    .timestamp(LocalDateTime.now())
                    .build();

            repo.save(log);

        } catch (Exception e) {
            throw new RuntimeException("Failed to write audit log", e);
        }
    }
}
