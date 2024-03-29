package dev.vio.InventorySys.controller;

import dev.vio.InventorySys.entity.Customer;
import dev.vio.InventorySys.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/list")
    public String listCustomers(Model model) {

        List<Customer> customers = customerService.findAll();

        model.addAttribute("customers", customers);

        return "customers/list-customers.html";
    }

    @GetMapping("/addCustomerForm")
    public String addCustomer(Model model) {

        Customer customer = new Customer();

        model.addAttribute("customer", customer);

        return "customers/addCustomerForm.html";
    }

    @PostMapping("/saveCustomer")
    public String saveCustomer(@ModelAttribute("customer") Customer customer) {

        customerService.save(customer);

        return "redirect:/products/list";
    }
}
