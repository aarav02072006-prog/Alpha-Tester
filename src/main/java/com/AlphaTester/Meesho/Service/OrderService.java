
package com.AlphaTester.Meesho.Service;

import com.AlphaTester.Meesho.Dto.ApiDtos;
import com.AlphaTester.Meesho.Model.CustomerOrder;
import com.AlphaTester.Meesho.Model.OrderItem;
import com.AlphaTester.Meesho.Model.Product;
import com.AlphaTester.Meesho.Repository.CustomerOrderRepository;
import com.AlphaTester.Meesho.Repository.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final CustomerOrderRepository orderRepository;

    public OrderService(
            ProductRepository productRepository,
            CustomerOrderRepository orderRepository
    ) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public ApiDtos.OrderResponse createOrder(
            ApiDtos.CreateOrderRequest request
    ) {
        CustomerOrder order = new CustomerOrder();

        order.setBuyerName(request.buyerName().trim());
        order.setBuyerEmail(request.buyerEmail().trim());
        order.setBuyerPhone(request.buyerPhone().trim());
        order.setShippingAddress(request.shippingAddress().trim());

        BigDecimal total = BigDecimal.ZERO;

        // Combine duplicate product IDs.
        Map<Long, Integer> quantities = new TreeMap<>();

        for (ApiDtos.OrderItemRequest item : request.items()) {
            quantities.merge(
                    item.productId(),
                    item.quantity(),
                    Integer::sum
            );
        }

        List<OrderItem> orderItems = new ArrayList<>();

        // Process products in sorted ID order.
        for (Map.Entry<Long, Integer> entry : quantities.entrySet()) {

            Long productId = entry.getKey();
            int quantity = entry.getValue();

            Product product = productRepository
                    .findActiveByIdForUpdate(productId)
                    .orElseThrow(() -> new ResponseStatusException(
                            NOT_FOUND,
                            "Product not found: " + productId
                    ));

            if (quantity <= 0) {
                throw new ResponseStatusException(
                        CONFLICT,
                        "Quantity must be greater than zero"
                );
            }

            if (product.getStock() < quantity) {
                throw new ResponseStatusException(
                        CONFLICT,
                        "Insufficient stock for product: "
                                + product.getName()
                );
            }

            // Reduce stock.
            product.setStock(product.getStock() - quantity);

            // Calculate prices using the database price.
            BigDecimal unitPrice = product.getPrice();

            BigDecimal lineTotal = unitPrice.multiply(
                    BigDecimal.valueOf(quantity)
            );

            total = total.add(lineTotal);

            orderItems.add(new OrderItem(
                    product.getId(),
                    product.getName(),
                    quantity,
                    unitPrice
            ));
        }

        order.setTotalAmount(total);

        for (OrderItem item : orderItems) {
            order.addItem(item);
        }

        // Save order and its items.
        CustomerOrder saved = orderRepository.save(order);

        List<ApiDtos.OrderItemResponse> itemResponses =
                saved.getItems()
                        .stream()
                        .map(item -> new ApiDtos.OrderItemResponse(
                                item.getProductId(),
                                item.getProductName(),
                                item.getQuantity(),
                                item.getUnitPrice(),
                                item.getUnitPrice().multiply(
                                        BigDecimal.valueOf(
                                                item.getQuantity()
                                        )
                                )
                        ))
                        .toList();

        return new ApiDtos.OrderResponse(
                saved.getId(),
                saved.getBuyerName(),
                saved.getBuyerEmail(),
                saved.getTotalAmount(),
                saved.getStatus(),
                saved.getCreatedAt(),
                itemResponses
        );
    }

    @Transactional(readOnly = true)
    public ApiDtos.OrderResponse getOrder(Long orderId) {

        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Order not found: " + orderId
                ));

        List<ApiDtos.OrderItemResponse> itemResponses =
                order.getItems()
                        .stream()
                        .map(item -> new ApiDtos.OrderItemResponse(
                                item.getProductId(),
                                item.getProductName(),
                                item.getQuantity(),
                                item.getUnitPrice(),
                                item.getUnitPrice().multiply(
                                        BigDecimal.valueOf(
                                                item.getQuantity()
                                        )
                                )
                        ))
                        .toList();

        return new ApiDtos.OrderResponse(
                order.getId(),
                order.getBuyerName(),
                order.getBuyerEmail(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                itemResponses
        );
    }
}
