package dev.vio.InventorySys.controller;

import dev.vio.InventorySys.entity.Product;
import dev.vio.InventorySys.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/list")
    public String listProducts(Model model) {

        List<Product> products = productService.findAll();

        model.addAttribute("products", products);

        return "products/list-products.html";
    }

    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model model) {

        Product product = new Product();

        model.addAttribute("product", product);

        return "products/addProductForm.html";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("product") Product product) {

        productService.save(product);

        return "redirect:/products/list";
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("productId") int id, Model model) {

        Product product = productService.findById(id);

        model.addAttribute("product", product);

        return "products/addProductForm";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("productId") int id) {

        Product product = productService.findById(id);

        productService.deleteById(id);

        return "redirect:/products/list";
    }

    @GetMapping("/generateInvoice")
    public String generateInvoice(Model model) {

        List<Product> products = productService.findAll();

        model.addAttribute("products", products);

        return "products/generateInvoiceForm";
    }


}
