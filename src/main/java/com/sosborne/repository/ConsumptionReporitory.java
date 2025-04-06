package com.sosborne.repository;

import com.sosborne.model.entity.Consumption;
import com.sosborne.model.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.UUID;

@Repository
public interface ConsumptionReporitory extends JpaRepository<Consumption,UUID> {
    ArrayList<Consumption> findByReservationInOrderByDateAsc(ArrayList<Reservation> lstReservation);
}
