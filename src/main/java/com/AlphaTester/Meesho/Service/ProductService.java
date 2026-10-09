package com.AlphaTester.Meesho.Service;

import com.AlphaTester.Meesho.Dto.ApiDtos;
import com.AlphaTester.Meesho.Model.Product;
import com.AlphaTester.Meesho.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<ApiDtos.ProductResponse> getProducts(
            String search,
            Long categoryId,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }

        Sort.Direction sortDirection;

        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (Exception ex) {
            throw new IllegalArgumentException("direction must be asc or desc");
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy)
        );

        String keyword = search == null ? "" : search.trim();

        Page<Product> result;

        if (categoryId != null && !keyword.isEmpty()) {
            result = productRepository
                    .findByActiveTrueAndCategory_IdAndNameContainingIgnoreCase(
                            categoryId, keyword, pageable
                    );
        } else if (categoryId != null) {
            result = productRepository
                    .findByActiveTrueAndCategory_Id(
                            categoryId, pageable
                    );
        } else if (keyword.isEmpty()) {
            result = productRepository
                    .findByActiveTrue(pageable);
        } else {
            result = productRepository
                    .findByActiveTrueAndNameContainingIgnoreCase(
                            keyword, pageable
                    );
        }

        return result.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ApiDtos.ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .filter(Product::isActive)
                .orElseThrow(()-> new ResponseStatusException(
                        NOT_FOUND,"Product not found"
                ));
        return toResponse(product);
    }

    private ApiDtos.ProductResponse toResponse(Product product) {
        return new ApiDtos.ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getOriginalPrice(),
                product.getImageUrl(),
                product.getStock(),
                product.getRating(),
                product.getReviewCount(),
                product.getCategory()==null?null:product.getCategory().getId(),
                product.getCategory()==null?null:product.getCategory().getName()
        );
    }
}