package com.AlphaTester.Meesho.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(
        name = "inventory_reservation",
        indexes = {
                @Index(
                        name = "idx_reservation_status_expiry",
                        columnList = "status,expires_at"
                )
        }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;
    @Column(nullable = false)
    private Integer quantity;
    @Column(nullable = false,length = 200)
    private String buyerEmail;
    @Column(nullable = false,length = 20)
    private String status = "ACTIVE";
    @Column(nullable = false)
    private Instant createdAt = Instant.now();
    @Column(nullable = false)
    private Instant expiresAt;

}
