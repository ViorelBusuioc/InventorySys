package dev.vio.InventorySys.dao;

import dev.vio.InventorySys.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {


}
