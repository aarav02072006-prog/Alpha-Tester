package com.AlphaTester.Meesho.Repository;

import com.AlphaTester.Meesho.Model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {
    @EntityGraph(attributePaths = {"category"})
    Page<Product> findByActiveTrue(Pageable pageable);
    @EntityGraph(attributePaths = {"category"})
    Page<Product> findByActiveTrueAndNameContainingIgnoreCase(
            String name,Pageable pageable
    );
    @EntityGraph(attributePaths = {"category"})
    Page<Product> findByActiveTrueAndCategory_Id(
            Long categoryId,Pageable pageable
    );

    @EntityGraph(attributePaths = {"category"})
    Page<Product> findByActiveTrueAndCategory_IdAndNameContainingIgnoreCase(
            Long categoryId,String name,Pageable pageable
    );
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.active=true")
    Optional<Product> findActiveByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id=:id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);


}
