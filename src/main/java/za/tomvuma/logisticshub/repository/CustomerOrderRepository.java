package za.tomvuma.logisticshub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.tomvuma.logisticshub.entity.CustomerOrder;

import java.util.List;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    // Find all orders for a given customer
    List<CustomerOrder> findByCustomerId(Long customerId);

    // Optional: find by status for filtering
    List<CustomerOrder> findByCustomerIdAndStatus(Long customerId, String status);
}

