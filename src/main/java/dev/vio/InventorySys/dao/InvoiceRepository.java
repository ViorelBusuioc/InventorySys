package dev.vio.InventorySys.dao;

import dev.vio.InventorySys.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {


}
