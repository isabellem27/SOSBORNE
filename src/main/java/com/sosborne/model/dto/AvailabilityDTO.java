package com.sosborne.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityDTO {
    private int day;
    private LocalTime beginhour;
    private LocalTime endhour;
    private Double price;

    @Override
    public String toString(){
        return this.day+" "+this.beginhour+" "+this.endhour+" "+ this.price.toString();
    }
}
