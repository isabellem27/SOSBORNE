package com.sosborne.controller;
import com.sosborne.model.dto.AddressDTO;
import com.sosborne.model.dto.ReservationDTO;
import com.sosborne.model.dto.ReservationSummaryDTO;
import com.sosborne.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor

public class ReservationController {
    private final ReservationService reservationService;
    /**
     * Endpoint to reserve a Borne (charging station) for a user.
     */
    @PostMapping("/reserve")
    public ResponseEntity<ReservationSummaryDTO> reserveBorne(@RequestBody ReservationDTO reservationDTO) {
        ReservationSummaryDTO reservationSummary = reservationService.reserveBorne(reservationDTO);
        return new ResponseEntity<>(reservationSummary, HttpStatus.CREATED);
    }

    /**
     * Endpoint to get all reservations for a user by their email.
     */
    @GetMapping("/user/{userEmail}")
    public ResponseEntity<List<ReservationSummaryDTO>> getReservationsByUserEmail(@PathVariable String userEmail) {
        List<ReservationSummaryDTO> reservations = reservationService.getReservationsByUserEmail(userEmail);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    /**
     * Endpoint to get all reservations for a specific Borne by its number and address.
     */
    @PostMapping("/borne/{numBorne}")
    public ResponseEntity<List<ReservationSummaryDTO>> getReservationsByBorne(@PathVariable int numBorne, @RequestBody AddressDTO addressDTO) {
        List<ReservationSummaryDTO> reservations = reservationService.getReservationsByBorne(numBorne, addressDTO);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    /**
     * Endpoint to cancel a reservation by its ID.
     */
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<String> cancelReservation(@PathVariable Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return new ResponseEntity<>("Reservation cancelled successfully", HttpStatus.OK);
    }
}