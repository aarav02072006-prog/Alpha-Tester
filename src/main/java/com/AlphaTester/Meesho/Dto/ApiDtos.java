package com.AlphaTester.Meesho.Dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.antlr.v4.runtime.misc.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class ApiDtos {
    private ApiDtos(){}

    public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        BigDecimal originalPrice,
        String imageUrl,
        Integer stock,
        Double rating,
        Integer reviewCount,
        Long categoryId,
        String categoryName
    ){}

    public record CategoryResponse(
            Long id,
            String name,
            String description
    ){}

    public record OrderItemRequest(
            @NotNull @Positive Long productId,
            @NotNull @Min(1) @Max(100) Integer quantity
    ){}

    public record CreateOrderRequest(
            @NotBlank @Size(max=150)
            String buyerName,@NotBlank @Email @Size(max=200) String buyerEmail,
            @NotBlank @Size(max = 30) String buyerPhone,
            @NotBlank @Size(max = 1000) String shippingAddress,
            @NotEmpty List<@Valid OrderItemRequest> items
    ){}

    public record OrderItemResponse(
            Long productId,
            String productName,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal lineTotal
    ){}
    public record OrderResponse(
            Long orderId,
            String buyerName,
            String buyerEmail,
            BigDecimal totalAmount,
            String status,
            Instant createdAt,
            List<OrderItemResponse> items
    ){}

    public record ReservationRequest(
            @NotNull @Positive Long productId,
            @NotNull @Min(1) @Max(100) Integer quantity,
            @NotBlank @Email @Size(max = 200) String buyerEmail
    ) {}

    public record ReservationResponse(
            Long reservationId,
            Long productId,
            Integer quantity,
            String buyerEmail,
            String status,
            Instant expiresAt
    ){}

    public record MessageResponse(String message){}
}
