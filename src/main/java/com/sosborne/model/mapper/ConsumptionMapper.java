package com.sosborne.model.mapper;

import com.sosborne.model.dto.ConsumptionDTO;
import com.sosborne.model.entity.Consumption;
import com.sosborne.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsumptionMapper {
    private final AddressMapper addressMapper;
    private final ReservationService reservationService;

    public Consumption ConsumptionDTOToEntity(ConsumptionDTO consumptionDTO) {
        Consumption consumption = new Consumption();

        consumption.setNbkwh(consumptionDTO.getNbkwh());
        consumption.setDate(consumptionDTO.getDate());
        consumption.setBeginhour(consumptionDTO.getBeginhour());
        consumption.setEndhour(consumptionDTO.getEndhour());
        consumption.setReservation(reservationService.getReservationByConsumption(consumptionDTO));
        return consumption;
    }


    public ConsumptionDTO ConsumptionToDTO(Consumption consumption) {
        ConsumptionDTO consumptionDTO = new ConsumptionDTO();

        consumptionDTO.setNbkwh(consumption.getNbkwh());
        consumptionDTO.setDate(consumption.getDate());
        consumptionDTO.setBeginhour(consumption.getBeginhour());
        consumptionDTO.setEndhour(consumption.getEndhour());
        consumptionDTO.setEmail(consumption.getReservation().getUser().getEmail());
        consumptionDTO.setNumborne(consumption.getReservation().getBorne().getNumborne());
        consumptionDTO.setAddress(addressMapper.addressToDTO(consumption.getReservation().getBorne().getAddress()));

        return consumptionDTO;
    }
}
