package com.themall.itemservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Document(collection = "items")
public class Item {
    @Id
    private String itemId; // MongoDB内部ID：数据库操作用
    
    @Indexed(unique = true)
    private String upc; // 业务标识，有实际含义, 防止重复录入
    
    private String itemName;
    private BigDecimal unitPrice;
    private List<String> pictureUrls;
    private Map<String, Object> metadata;

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    
    public String getUpc() { return upc; }
    public void setUpc(String upc) { this.upc = upc; }
    
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    public List<String> getPictureUrls() { return pictureUrls; }
    public void setPictureUrls(List<String> pictureUrls) { this.pictureUrls = pictureUrls; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}