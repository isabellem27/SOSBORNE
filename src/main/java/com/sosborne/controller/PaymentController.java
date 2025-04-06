package com.sosborne.controller;

import com.sosborne.enu.PaymentStatus;
import com.sosborne.model.dto.PaymentRequestDTO;
import com.sosborne.model.entity.Payment;
import com.sosborne.repository.PaymentRepository;
import com.sosborne.service.PaymentService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;



    /**
     * API to create a new payment
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequestDTO paymentRequest) {
        try {
            String clientSecret = paymentService.createPayment(paymentRequest);
            return ResponseEntity.ok(clientSecret);
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body("Payment failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * API to validate a payment based on PaymentIntent ID
     */
    @GetMapping("/validate/{paymentIntentId}")
    public ResponseEntity<?> validatePayment(@PathVariable String paymentIntentId) {
        try {
            PaymentStatus status = paymentService.validatePayment(paymentIntentId);
            return ResponseEntity.ok(status);
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body("Validation failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * API to get payment details by ID
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPayment(@PathVariable UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .map(ResponseEntity::ok)  // If found, return 200 OK with Payment object
                .orElseGet(() -> ResponseEntity.notFound().build());  // Return 404 Not Found if missing
    }
}
