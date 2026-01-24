package za.tomvuma.logisticshub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.tomvuma.logisticshub.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}

