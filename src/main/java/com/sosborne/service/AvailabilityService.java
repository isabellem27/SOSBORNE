package com.sosborne.service;


import com.sosborne.model.dto.AvailabilityDTO;
import com.sosborne.model.entity.Availability;
import com.sosborne.model.entity.Borne;
import com.sosborne.model.entity.Day;
import com.sosborne.model.mapper.AvailabilityMapper;
import com.sosborne.repository.AvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class AvailabilityService {
    private final AvailabilityRepository availabilityRepository;
    private final DayService dayService;
    private final AvailabilityMapper availabilityMapper;


    public ArrayList<AvailabilityDTO> getAvailabilityByBorne(Borne borne){
        ArrayList<AvailabilityDTO> lstDTO = new ArrayList<>();

        ArrayList<Availability> lstAvailability = availabilityRepository.findByBorne(borne);
        for (Availability item:lstAvailability){
            lstDTO.add(availabilityMapper.availabilityToDTO(item));
        }

        //tri sur le numéro de jour et date de début de période
        lstDTO.sort(Comparator.comparing(AvailabilityDTO::getDay)
                .thenComparing(AvailabilityDTO::getBeginhour));

        return lstDTO;
    }

    public void addAvailabilitiesFromBorneDTO(ArrayList<AvailabilityDTO> lstAvailabilityDTO, Borne borne){
        Day day;
        for (AvailabilityDTO item: lstAvailabilityDTO){
            day = dayService.getByNum(item.getDay());
            availabilityRepository.save(new Availability(null, item.getBeginhour(), item.getEndhour(),item.getPrice()
                    ,borne, day) );
        }
    }

    public void replaceLstByBorne(Borne borne, ArrayList<AvailabilityDTO> lstDTO){
        //Annule et remplace les disponibilités
        deleteByBorne(borne);
        addAvailabilitiesFromBorneDTO(lstDTO, borne);
    }

    public void deleteByBorne(Borne borne){
        availabilityRepository.deleteByBorne(borne);
    }
}
