package com.sosborne.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentResponseDTO {
    private UUID id;
    private String paymentIntentId;
    private BigDecimal amount;
    private String paymentMethod;
    private String currency;
    private UUID reservationId; //  Added for clarity
    private UUID userId; // Added for clarity
    private LocalDateTime createdAt; // Matches `Payment` entity field
}