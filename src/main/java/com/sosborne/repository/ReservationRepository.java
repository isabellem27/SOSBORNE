package com.sosborne.repository;

import com.sosborne.model.entity.Borne;
import com.sosborne.model.entity.Reservation;
import com.sosborne.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByBorneAndDateAndUser(Borne borne, LocalDate date, User user);

    /**
     * Checks if a time slot overlaps with existing reservations.
     */
    List<Reservation> findByBorneAndDateAndBeginhourBeforeAndEndhourAfter(
            Borne borne, LocalDate date, LocalTime beginHour, LocalTime endHour);

    ArrayList<Reservation> findByBorneAndDateBetween(Borne borne, LocalDate date1, LocalDate date2);

    List<Reservation> findByUser(User user);
    List<Reservation> findByBorne(Borne borne);
    Optional<Reservation> findById(Long reservationId);
    Optional<Reservation> findById(UUID id);
}


