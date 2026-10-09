package com.AlphaTester.Meesho.Controller;

import com.AlphaTester.Meesho.Dto.ApiDtos;
import com.AlphaTester.Meesho.Model.Category;
import com.AlphaTester.Meesho.Repository.CategoryRepository;
import com.AlphaTester.Meesho.Service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {
    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    public ProductController(
            ProductService productService,
            CategoryRepository categoryRepository
    ) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/health")
    public ApiDtos.MessageResponse health() {
        return new ApiDtos.MessageResponse("Meesho API is running");
    }

    @GetMapping("/products")
    public Page<ApiDtos.ProductResponse> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return productService.getProducts(
                search, categoryId, page, size, sortBy, direction
        );
    }

    @GetMapping("/products/{id}")
    public ApiDtos.ProductResponse getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @GetMapping("/categories")
    public List<ApiDtos.CategoryResponse> getCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> new ApiDtos.CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getDescription()
                ))
                .toList();
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiDtos.CategoryResponse createCategory(
            @RequestBody Category category
    ) {
        if (category.getName() == null
                || category.getName().isBlank()) {
            throw new IllegalArgumentException(
                    "Category name is required"
            );
        }

        Category saved = categoryRepository.save(
                new Category(
                        category.getName().trim(),
                        category.getDescription()
                )
        );

        return new ApiDtos.CategoryResponse(
                saved.getId(),
                saved.getName(),
                saved.getDescription()
        );
    }
}
