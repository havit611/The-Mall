package com.themall.itemservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ItemRequest {
    @NotBlank(message = "UPC is required")
    private String upc;
    
    @NotBlank(message = "Item name is required")
    private String itemName;
    
    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be positive")
    private BigDecimal unitPrice;
    
    private List<String> pictureUrls;
    private Map<String, Object> metadata;

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