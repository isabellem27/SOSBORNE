package com.sosborne.model.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    @NotNull(message = "User email is required")
    private String userEmail;

    @NotNull(message = "Address is required")
    private AddressDTO address;

    @NotNull(message = "Reservation date is required")
    private LocalDate date;

    @NotNull(message = "Start time is required")
    private LocalTime beginHour;

    @NotNull(message = "End time is required")
    private LocalTime endHour;

    private int numBorne;

}

