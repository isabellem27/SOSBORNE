package com.sosborne.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConsumptionDTO {
    private String email;
    private Double nbkwh;
    private LocalDate date;
    private LocalTime beginhour;
    private LocalTime endhour;
    private int numborne;
    private AddressDTO address;
}
