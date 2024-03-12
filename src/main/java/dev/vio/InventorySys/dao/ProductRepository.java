package dev.vio.InventorySys.dao;

import dev.vio.InventorySys.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {


}
