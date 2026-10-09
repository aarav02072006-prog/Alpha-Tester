package com.AlphaTester.Meesho.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "products",
        indexes = {
            @Index(name=
            "idx_product_name",columnList = "name"),
                @Index(name =
                "idx_product_category",columnList = "category_id"),
                @Index(name=
                "idx_product_price",columnList = "price")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,length = 200)
    private String name;
    @Column(length = 2000)
    private String description;
    @Column(nullable = false,precision = 12,scale = 2)
    private BigDecimal price;
    @Column(precision = 12,scale = 2)
    private BigDecimal originalPrice;
    @Column(length = 100)
    private String imageUrl;
    @Column(nullable = false)
    private Integer stock;
    @Column(nullable = false)
    private Double rating;
    @Column(nullable = false)
    private Integer reviewCount;
    @Column(nullable = false)
    private boolean active;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

}
