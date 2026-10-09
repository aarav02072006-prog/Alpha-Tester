package com.AlphaTester.Meesho.Controller;

import com.AlphaTester.Meesho.Dto.ApiDtos;
import com.AlphaTester.Meesho.Service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiDtos.ReservationResponse reserve(
            @Valid @RequestBody ApiDtos.ReservationRequest request
    ) {
        return reservationService.reserve(request);
    }

    @GetMapping("/{id}")
    public ApiDtos.ReservationResponse getReservation(@PathVariable Long id) {
        return reservationService.getReservation(id);
    }

    @PostMapping("/{id}/cancel")
    public ApiDtos.ReservationResponse cancel(
            @PathVariable Long id,
            @RequestParam String buyerEmail
    ) {
        return reservationService.cancel(id, buyerEmail);
    }
}
