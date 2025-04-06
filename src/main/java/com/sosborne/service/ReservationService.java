package com.sosborne.service;

import com.sosborne.exception.ReservationConflictException;
import com.sosborne.exception.ResourceNotFoundException;
import com.sosborne.model.dto.*;
import com.sosborne.model.entity.Borne;
import com.sosborne.model.entity.Reservation;
import com.sosborne.model.entity.User;
import com.sosborne.model.mapper.ReservationMapper;
import com.sosborne.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class  ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final BorneService borneService;
    private final ReservationMapper reservationMapper;


    /**
     * Reserves a Borne (charging station) for a user using email and address.
     */
    @Transactional
    public ReservationSummaryDTO reserveBorne(ReservationDTO reservationDTO) {
        // Find user by email
        User user = userService.getByEmail(reservationDTO.getUserEmail());

        // Fetch the Borne using BorneService
        Borne borne = borneService.getBorne(reservationDTO.getAddress(), reservationDTO.getNumBorne());

        // Check for reservation conflicts
        List<Reservation> conflicts = reservationRepository.findByBorneAndDateAndBeginhourBeforeAndEndhourAfter(
                borne, reservationDTO.getDate(), reservationDTO.getBeginHour(), reservationDTO.getEndHour()
        );

        if (!conflicts.isEmpty()) {
            throw new ReservationConflictException("Time slot is already booked.");
        }

        // Create and save reservation
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBorne(borne);
        reservation.setDate(reservationDTO.getDate());
        reservation.setBeginhour(reservationDTO.getBeginHour());
        reservation.setEndhour(reservationDTO.getEndHour());

        reservationRepository.save(reservation);

        // Convert to DTO and return response
        return reservationMapper.toDto(reservation);
    }

    public List<ReservationSummaryDTO> getReservationsByUserEmail(String userEmail) {
        User user = userService.getByEmail(userEmail);

        List<Reservation> reservations = reservationRepository.findByUser(user);
        return reservations.stream().map(reservationMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Retrieves all reservations for a specific Borne based on its number (numBorne).
     */
    public List<ReservationSummaryDTO> getReservationsByBorne(int numBorne, AddressDTO addressDTO) {
        Borne borne = borneService.getBorne(addressDTO, numBorne);

        List<Reservation> reservations = reservationRepository.findByBorne(borne);
        return reservations.stream().map(reservationMapper::toDto).collect(Collectors.toList());
    }

    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + reservationId));
        reservationRepository.delete(reservation);
    }

    public ArrayList<Reservation> getReservationByFilter(FilterDTO filterDTO){
        //Récupère la liste des réservations en fonction du filtrage de l'utilisateur
        ArrayList<Reservation> lstEntity = reservationRepository.findByBorneAndDateBetween(
                borneService.getBorne(filterDTO.getAddress(), filterDTO.getNumborne()),
                filterDTO.getBegindate(), filterDTO.getEnddate() );
        return lstEntity;
    }

    public Reservation getReservationByConsumption(ConsumptionDTO consumptionDTO){

        Borne borne = borneService.getBorne(consumptionDTO.getAddress(), consumptionDTO.getNumborne());
        Reservation reservation = reservationRepository.findByBorneAndDateAndUser(borne, consumptionDTO.getDate()
                        ,userService.getByEmail(consumptionDTO.getEmail()))
                .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable pour la date: --> "
                        + consumptionDTO.getDate()+" et l'utilisateur dont l'email est "+consumptionDTO.getEmail()));

        return reservation;
    }

}
