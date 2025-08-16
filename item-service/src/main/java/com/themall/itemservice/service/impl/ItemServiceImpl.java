package com.themall.itemservice.service.impl;

import com.themall.itemservice.dto.ItemRequest;
import com.themall.itemservice.entity.Inventory;
import com.themall.itemservice.entity.Item;
import com.themall.itemservice.repository.ItemRepository;
import com.themall.itemservice.repository.InventoryRepository;
import com.themall.itemservice.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    
    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;
    
    public ItemServiceImpl(ItemRepository itemRepository, InventoryRepository inventoryRepository) {
        this.itemRepository = itemRepository;
        this.inventoryRepository = inventoryRepository;
    }


    //
    @Override
    public Item createItem(ItemRequest request) {
        // 1. 验证UPC唯一性：检查数据库中是否已存在相同UPC的商品，防止重复录入
        if (itemRepository.existsByUpc(request.getUpc())) {
            throw new RuntimeException("Item with UPC " + request.getUpc() + " already exists");
        }

        // 2. 创建商品实体对象：将请求DTO转换为实体类
        Item item = new Item();
        item.setUpc(request.getUpc());
        item.setItemName(request.getItemName());
        item.setUnitPrice(request.getUnitPrice());
        item.setPictureUrls(request.getPictureUrls());
        item.setMetadata(request.getMetadata());

        // 3. 保存商品到数据库
        Item saved = itemRepository.save(item);

        // 4. 创建初始库存记录：为新商品自动创建库存记录，初始数量为0
        Inventory inventory = new Inventory();
        inventory.setItemId(saved.getItemId());  // 关联刚创建的商品ID
        inventory.setAvailableUnits(0);

        // 5. 保存库存记录到数据库：完成商品-库存的关联关系
        inventoryRepository.save(inventory);
        
        return saved;
    }

    @Override
    public Item getItemById(String itemId) {
        return itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    @Override
    public Item getItemByUpc(String upc) {
        return itemRepository.findByUpc(upc)
            .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    // TODO： could update this to a ItemUpdateStrategy interface later
    @Override
    public Item updateItem(String itemId, ItemRequest request) {
        Item item = getItemById(itemId);
        
        if (request.getItemName() != null) {
            item.setItemName(request.getItemName());
        }
        if (request.getUnitPrice() != null) {
            item.setUnitPrice(request.getUnitPrice());
        }
        if (request.getPictureUrls() != null) {
            item.setPictureUrls(request.getPictureUrls());
        }
        if (request.getMetadata() != null) {
            item.setMetadata(request.getMetadata());
        }
        
        return itemRepository.save(item);
    }

    @Override
    public void deleteItem(String itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new RuntimeException("Item not found");
        }
        
        itemRepository.deleteById(itemId);
        inventoryRepository.deleteByItemId(itemId);
    }

//    @Override
//    public List<Item> getAllItems() {
//        return itemRepository.findAll();
//    }
    // Updated: pagination
@Override
public Page<Item> getAllItems(int page, int size, String sortBy, String sortDir) {
    // 根据controller调用的sortDir值，解析排序方向（默认升序）
    Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
    return itemRepository.findAll(pageable);
}
}