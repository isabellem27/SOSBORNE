package com.sosborne.model.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
//utilisé pour le suivi des consommations
public class ConsumptionResponseDTO {
    private Double nbkwh;
    private LocalDateTime begindate;
    private LocalDateTime enddate;
    private int numSlice;

}


