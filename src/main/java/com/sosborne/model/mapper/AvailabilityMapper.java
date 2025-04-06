package com.sosborne.model.mapper;

import com.sosborne.model.dto.AvailabilityDTO;
import com.sosborne.model.entity.Availability;
import com.sosborne.service.DayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AvailabilityMapper {
    private final DayService dayService;

    public Integer getIdxDay(String name) {
        Map<String, Integer> dayIndexes = new HashMap<>();

        dayIndexes.put("lundi",1);
        dayIndexes.put("mardi",2);
        dayIndexes.put("mercredi",3);
        dayIndexes.put("jeudi",4);
        dayIndexes.put("vendredi",5);
        dayIndexes.put("samedi",6);
        dayIndexes.put("dimanche",7);

        return dayIndexes.get(name);
    }


    public AvailabilityDTO availabilityToDTO(Availability availability){
        AvailabilityDTO availabilityDTO = new AvailabilityDTO();

        availabilityDTO.setDay(getIdxDay(availability.getDay().getDay()));
        availabilityDTO.setBeginhour(availability.getBeginhour());
        availabilityDTO.setEndhour(availability.getEndhour());
        availabilityDTO.setPrice(availability.getKwhprice());

        return availabilityDTO;
    }

    public Availability availabilityDTOToEntity(AvailabilityDTO availabilityDTO){
        Availability availability = new Availability();

        availability.setDay(dayService.getByNum(availabilityDTO.getDay()));
        availability.setBeginhour(availabilityDTO.getBeginhour());
        availability.setEndhour(availabilityDTO.getEndhour());
        availability.setKwhprice(availabilityDTO.getPrice());

        return availability;
    }

}
