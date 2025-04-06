package com.sosborne.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class PaymentRequestDTO {
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "Payment method cannot be blank")
    private String paymentMethod;

    @NotBlank(message = "Currency cannot be blank")
    private String currency;

    @NotNull(message = "User ID cannot be null")
    private UUID userId;

    @NotNull(message = "Reservation ID cannot be null")
    private UUID reservationId; // Added to link payment to reservation

}
