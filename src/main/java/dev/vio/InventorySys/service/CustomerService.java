package dev.vio.InventorySys.service;

import dev.vio.InventorySys.entity.Customer;

import java.util.List;

public interface CustomerService {

    List<Customer> findAll();

    Customer findById(int id);

    Customer save(Customer customer);

    void deleteById(int id);
}
