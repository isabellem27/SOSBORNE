package com.sosborne.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    private String street;
    private String zipcode;
    private String town;


    public String getAddress() {
        return this.street + ", " + this.zipcode + " " + this.town;
    }
}

