package com.AlphaTester.Meesho.Service;

import com.AlphaTester.Meesho.Dto.ApiDtos;
import com.AlphaTester.Meesho.Model.InventoryReservation;
import com.AlphaTester.Meesho.Model.Product;
import com.AlphaTester.Meesho.Repository.InventoryReservationRepository;
import com.AlphaTester.Meesho.Repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class ReservationService {

    private final ProductRepository productRepository;
    private final InventoryReservationRepository reservationRepository;

    public ReservationService(
            ProductRepository productRepository,
            InventoryReservationRepository reservationRepository
    ) {
        this.productRepository = productRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public ApiDtos.ReservationResponse reserve(ApiDtos.ReservationRequest request) {
        Product product = productRepository
                .findActiveByIdForUpdate(request.productId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found"
                ));

        if (product.getStock() < request.quantity()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Insufficient stock"
            );
        }

        product.setStock(product.getStock() - request.quantity());

        InventoryReservation reservation = new InventoryReservation();
        reservation.setProductId(product.getId());
        reservation.setQuantity(request.quantity());
        reservation.setBuyerEmail(request.buyerEmail().trim());
        reservation.setExpiresAt(Instant.now().plusSeconds(180));
        reservation.setStatus("ACTIVE");

        InventoryReservation saved =
                reservationRepository.save(reservation);

        return toResponse(saved);
    }

    @Transactional
    public ApiDtos.ReservationResponse getReservation(Long id) {
        InventoryReservation reservation =
                reservationRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Reservation not found"
                        ));

        // Expire it immediately if the buyer checks after the deadline.
        if ("ACTIVE".equals(reservation.getStatus())
                && !reservation.getExpiresAt().isAfter(Instant.now())) {
            expireReservation(reservation);
        }

        return toResponse(reservation);
    }

    @Transactional
    public ApiDtos.ReservationResponse cancel(Long id, String buyerEmail) {
        InventoryReservation reservation =
                reservationRepository.findByIdForUpdate(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Reservation not found"
                        ));

        if (!reservation.getBuyerEmail().equalsIgnoreCase(
                buyerEmail.trim())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Reservation does not belong to this email"
            );
        }

        if ("ACTIVE".equals(reservation.getStatus())) {
            if (!reservation.getExpiresAt().isAfter(Instant.now())) {
                expireReservation(reservation);
            } else {
                Product product = productRepository
                        .findByIdForUpdate(reservation.getProductId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Reserved product not found"
                        ));

                product.setStock(
                        product.getStock() + reservation.getQuantity()
                );

                reservation.setStatus("CANCELLED");
            }
        }

        return toResponse(reservation);
    }

    @Scheduled(fixedDelay = 10000)
    public void expireReservations() {
        List<InventoryReservation> expired =
                reservationRepository
                        .findByStatusAndExpiresAtLessThanEqual(
                                "ACTIVE", Instant.now()
                        );

        for (InventoryReservation reservation : expired) {
            try {
                expireOne(reservation.getId());
            } catch (Exception ignored) {
                // A later scheduled run can retry failed expirations.
            }
        }
    }

    @Transactional
    public void expireOne(Long id) {
        InventoryReservation reservation =
                reservationRepository.findByIdForUpdate(id).orElse(null);

        if (reservation == null
                || !"ACTIVE".equals(reservation.getStatus())
                || reservation.getExpiresAt().isAfter(Instant.now())) {
            return;
        }

        expireReservation(reservation);
    }

    private void expireReservation(InventoryReservation reservation) {
        Product product = productRepository
                .findByIdForUpdate(reservation.getProductId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Reserved product not found"
                ));

        product.setStock(product.getStock() + reservation.getQuantity());
        reservation.setStatus("EXPIRED");
    }

    private ApiDtos.ReservationResponse toResponse(
            InventoryReservation reservation
    ) {
        return new ApiDtos.ReservationResponse(
                reservation.getId(),
                reservation.getProductId(),
                reservation.getQuantity(),
                reservation.getBuyerEmail(),
                reservation.getStatus(),
                reservation.getExpiresAt()
        );
    }
}