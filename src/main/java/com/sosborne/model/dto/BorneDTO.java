package com.sosborne.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BorneDTO {
    private String taughtname;
    private String marketname;
    private AddressDTO address;
    private int numborne;
    private double power;
    private String type;
    private boolean publicField;
    private int nbpoint;
    private Boolean working;
    private ArrayList<AvailabilityDTO> lstAvailability;

}
