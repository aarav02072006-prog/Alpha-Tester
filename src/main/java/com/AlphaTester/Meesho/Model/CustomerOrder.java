package com.AlphaTester.Meesho.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer_orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,length = 150)
    private String buyerName;
    @Column(nullable = false,length = 200)
    private String buyerEmail;
    @Column(nullable = false,length = 30)
    private String buyerPhone;
    @Column(nullable = false,length = 1000)
    private String shippingAddress;
    @Column(nullable = false,precision = 12,scale = 2)
    private BigDecimal totalAmount;
    @Column(nullable = false,length = 30)
    private String status = "CONFIRMED";
    private Instant createdAt = Instant.now();
    @OneToMany(mappedBy = "customerOrder",
                cascade = CascadeType.ALL,
                orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    public void addItem(OrderItem item) {
        items.add(item);
        item.setCustomerOrder(this);
    }
}
