package dev.vio.InventorySys.dao;

import dev.vio.InventorySys.entity.LineItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineItemRepository extends JpaRepository<LineItem, Integer> {


}
