package dev.vio.InventorySys.service;

import dev.vio.InventorySys.dao.ProductRepository;
import dev.vio.InventorySys.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(int id) {

        Optional<Product> result = productRepository.findById(id);
        Product product = null;
        if(result.isPresent()) {
            product = result.get();
        } else {
            throw new RuntimeException("Did not find the product with id: " + id);
        }

        return product;
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteById(int id) {
        productRepository.deleteById(id);
    }
}
