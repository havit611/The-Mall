package com.themall.orderservice.entity;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;
import java.math.BigDecimal;

@Table("order_items")
public class OrderItem {
    
    @PrimaryKeyClass
    public static class OrderItemKey implements Serializable {
        
        @PrimaryKeyColumn(name = "order_id", type = PrimaryKeyType.PARTITIONED)
        private String orderId;
        
        @PrimaryKeyColumn(name = "item_id", type = PrimaryKeyType.CLUSTERED)
        private String itemId;

        public OrderItemKey() {}

        public OrderItemKey(String orderId, String itemId) {
            this.orderId = orderId;
            this.itemId = itemId;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }
    }
    
    @PrimaryKey
    private OrderItemKey key;
    private String itemName;
    private Integer quantity;
    private BigDecimal unitPrice;

    public OrderItem() {}

    public OrderItem(String orderId, String itemId, String itemName, 
                    Integer quantity, BigDecimal unitPrice) {
        this.key = new OrderItemKey(orderId, itemId);
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public OrderItemKey getKey() {
        return key;
    }

    public void setKey(OrderItemKey key) {
        this.key = key;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}