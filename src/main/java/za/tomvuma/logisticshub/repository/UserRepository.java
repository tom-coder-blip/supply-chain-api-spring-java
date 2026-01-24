package za.tomvuma.logisticshub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.tomvuma.logisticshub.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
