package dev.vio.InventorySys.service;

import dev.vio.InventorySys.dao.LineItemRepository;
import dev.vio.InventorySys.entity.LineItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LineItemServiceImpl implements LineItemService {

    private LineItemRepository lineItemRepository;

    @Autowired
    public LineItemServiceImpl(LineItemRepository lineItemRepository) {
        this.lineItemRepository = lineItemRepository;
    }

    @Override
    public List<LineItem> findAll() {
        return lineItemRepository.findAll();
    }

    @Override
    public LineItem findById(int id) {

        Optional<LineItem> result = lineItemRepository.findById(id);
        LineItem lineItem = null;
        if(result.isPresent()) {
            lineItem = result.get();
        } else {
            throw new RuntimeException("Did not find the line item with id: " + id);
        }

        return lineItem;
    }

    @Override
    public LineItem save(LineItem lineItem) {
        return lineItemRepository.save(lineItem);
    }

    @Override
    public void deleteById(int id) {
        lineItemRepository.deleteById(id);
    }
}
