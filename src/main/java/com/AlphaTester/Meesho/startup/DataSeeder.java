package com.AlphaTester.Meesho.startup;

import com.AlphaTester.Meesho.Model.Category;
import com.AlphaTester.Meesho.Model.Product;
import com.AlphaTester.Meesho.Repository.CategoryRepository;
import com.AlphaTester.Meesho.Repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public DataSeeder(
            CategoryRepository categoryRepository,
            ProductRepository productRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        Category fashion = categoryRepository.save(
                new Category("Fashion", "Clothing and everyday fashion")
        );

        Category electronics = categoryRepository.save(
                new Category("Electronics", "Useful electronic accessories")
        );

        Category home = categoryRepository.save(
                new Category("Home", "Home and kitchen essentials")
        );

        List<Product> products = List.of(
                createProduct(
                        "Casual Cotton T-Shirt",
                        "Comfortable everyday cotton T-shirt.",
                        "499.00", "799.00",
                        "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=700",
                        100, 4.3, 128, fashion
                ),
                createProduct(
                        "Running Shoes",
                        "Lightweight shoes for everyday running and walking.",
                        "1299.00", "1999.00",
                        "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=700",
                        60, 4.5, 240, fashion
                ),
                createProduct(
                        "Wireless Headphones",
                        "Wireless headphones for music and calls.",
                        "1599.00", "2499.00",
                        "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=700",
                        45, 4.2, 96, electronics
                ),
                createProduct(
                        "Smart Watch",
                        "Smart watch with fitness tracking features.",
                        "1999.00", "2999.00",
                        "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=700",
                        35, 4.1, 78, electronics
                ),
                createProduct(
                        "Ceramic Coffee Mug",
                        "Reusable ceramic mug for tea and coffee.",
                        "249.00", "399.00",
                        "https://images.unsplash.com/photo-1514228742587-6b1558fcca3d?w=700",
                        120, 4.4, 62, home
                ),
                createProduct(
                        "Table Lamp",
                        "Minimal table lamp for study and bedroom use.",
                        "899.00", "1299.00",
                        "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=700",
                        25, 4.0, 34, home
                )
        );

        productRepository.saveAll(products);
    }

    private Product createProduct(
            String name,
            String description,
            String price,
            String originalPrice,
            String imageUrl,
            int stock,
            double rating,
            int reviewCount,
            Category category
    ) {
        Product product = new Product();

        product.setName(name);
        product.setDescription(description);
        product.setPrice(new BigDecimal(price));
        product.setOriginalPrice(new BigDecimal(originalPrice));
        product.setImageUrl(imageUrl);
        product.setStock(stock);
        product.setRating(rating);
        product.setReviewCount(reviewCount);
        product.setActive(true);
        product.setCategory(category);

        return product;
    }
}