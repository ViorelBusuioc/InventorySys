package dev.vio.InventorySys.service;

import dev.vio.InventorySys.entity.LineItem;

import java.util.List;

public interface LineItemService {

    List<LineItem> findAll();

    LineItem findById(int id);

    LineItem save(LineItem lineItem);

    void deleteById(int id);
}
