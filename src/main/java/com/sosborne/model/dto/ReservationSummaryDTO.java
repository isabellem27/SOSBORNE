package com.sosborne.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationSummaryDTO {

    private String email;
    private String terminalAddress;
    private LocalDate date;
    private LocalTime beginHour;
    private LocalTime endHour;
}
