package com.AlphaTester.Meesho.Repository;

import com.AlphaTester.Meesho.Model.InventoryReservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation,Long> {
    List<InventoryReservation> findByStatusAndExpiresAtLessThanEqual(
            String status, Instant time
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM InventoryReservation r WHERE r.id = :id")
    Optional<InventoryReservation> findByIdForUpdate(
            @Param("id") Long id
    );
}
