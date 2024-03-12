package dev.vio.InventorySys.service;

import dev.vio.InventorySys.dao.CustomerRepository;
import dev.vio.InventorySys.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer findById(int id) {

        Optional<Customer> result = customerRepository.findById(id);
        Customer customer = null;
        if(result.isPresent()) {
            customer = result.get();
        } else {
            throw new RuntimeException("Did not find the customer with id: " + id);
        }

        return customer;
    }

    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public void deleteById(int id) {
        customerRepository.deleteById(id);
    }
}
