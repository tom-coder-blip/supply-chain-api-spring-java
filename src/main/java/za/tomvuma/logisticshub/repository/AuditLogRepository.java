package za.tomvuma.logisticshub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.tomvuma.logisticshub.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {}
