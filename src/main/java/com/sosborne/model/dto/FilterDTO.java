package com.sosborne.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FilterDTO {
    private int numborne;
    private AddressDTO address;
    private LocalDate begindate;
    private LocalDate enddate;
    private String type;

}
