package com.sosborne.model.mapper;

import com.sosborne.model.dto.ReservationSummaryDTO;
import com.sosborne.model.entity.Reservation;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {
    public ReservationSummaryDTO toDto(Reservation reservation) {
        return new ReservationSummaryDTO(
                reservation.getUser().getEmail(),
                reservation.getBorne().getAddress().toString(),
                reservation.getDate(),
                reservation.getBeginhour(),
                reservation.getEndhour()
        );
    }
}
