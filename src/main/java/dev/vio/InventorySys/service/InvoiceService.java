package dev.vio.InventorySys.service;

import dev.vio.InventorySys.entity.Invoice;

import java.util.List;


public interface InvoiceService {

    List<Invoice> findAll();

    Invoice findById(int id);

    Invoice save(Invoice invoice);

    void deleteById(int id);

}
