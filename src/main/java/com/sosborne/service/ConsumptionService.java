package com.sosborne.service;

import com.sosborne.exception.BusinessLogicException;
import com.sosborne.exception.ResourceNotFoundException;
import com.sosborne.model.dto.ConsumptionDTO;
import com.sosborne.model.dto.ConsumptionResponseDTO;
import com.sosborne.model.dto.FilterDTO;
import com.sosborne.model.entity.Consumption;
import com.sosborne.model.mapper.ConsumptionMapper;
import com.sosborne.repository.ConsumptionReporitory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class ConsumptionService {
    private final ConsumptionReporitory consumptionReporitory;
    private final ConsumptionMapper consumptionMapper;
    private final ReservationService reservationService;

    private static String getYearWeek(LocalDate date) {
        int year = date.getYear();
        int week = date.get(WeekFields.ISO.weekOfYear());
        return year + "-W" + week; // Format "2024-W05"
    }

    private static LocalDate getWeekStart(String yearWeek) {
        String[] parts = yearWeek.split("-W");
        int year = Integer.parseInt(parts[0]);
        int week = Integer.parseInt(parts[1]);
        return LocalDate.ofYearDay(year, 1)
                .with(WeekFields.ISO.weekOfYear(), week)
                .with(WeekFields.ISO.dayOfWeek(), 1); // Lundi de la semaine
    }

    public static ArrayList<ConsumptionResponseDTO> aggregateByHour(ArrayList<Consumption> records) {
        ArrayList<ConsumptionResponseDTO> lstDTO = new ArrayList<>();
        int numSlice = 0;
        for (Consumption record : records) {
            LocalDateTime start = LocalDateTime.of(record.getDate(), record.getBeginhour());
            LocalDateTime end = LocalDateTime.of(record.getDate(), record.getEndhour());

            long totalMinutes = java.time.Duration.between(start, end).toMinutes();
            if (totalMinutes == 0) continue; // Éviter la division par zéro

            // Répartition proportionnelle de la consommation par minute
            double consumptionPerMinute = record.getNbkwh() / totalMinutes;

            LocalDateTime current = start.truncatedTo(java.time.temporal.ChronoUnit.HOURS);
            LocalDateTime nextHour = current.plusHours(1);

            while (current.isBefore(end)) {
                numSlice=numSlice+1;
                LocalDateTime segmentEnd = nextHour.isBefore(end) ? nextHour : end;
                long minutesInSegment = java.time.Duration.between(current, segmentEnd).toMinutes();
                double consumptionForHour = minutesInSegment * consumptionPerMinute;

                lstDTO.add(new ConsumptionResponseDTO(consumptionForHour, current, current, numSlice));

                // Passer à l'heure suivante
                current = nextHour;
                nextHour = nextHour.plusHours(1);
            }
        }

        return lstDTO;

    }

    public static ArrayList<ConsumptionResponseDTO> aggregateByDay(ArrayList<Consumption> records) {
        return (ArrayList<ConsumptionResponseDTO>) records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getDate(),
                        Collectors.summingDouble(r -> r.getNbkwh())
                ))
                .entrySet().stream()
                .map(entry -> new ConsumptionResponseDTO(
                        entry.getValue(),
                        entry.getKey().atTime(0, 0),
                        entry.getKey().atTime(0, 0),
                        0
                ))
                .sorted(Comparator.comparing(a -> a.getBegindate())) // Tri par date
                .collect(Collectors.toList());

    }

    public static ArrayList<ConsumptionResponseDTO> aggregateByWeek(ArrayList<Consumption> records) {
        return (ArrayList<ConsumptionResponseDTO>) records.stream()
                .collect(Collectors.groupingBy(
                        r -> getYearWeek(r.getDate()), // Regroupement par année + semaine
                        Collectors.summingDouble(r -> r.getNbkwh())
                ))
                .entrySet().stream()
                .map(entry -> {
                    LocalDate weekStart = getWeekStart(entry.getKey());
                    LocalDate weekEnd = weekStart.plusDays(6);
                    return new ConsumptionResponseDTO(
                            entry.getValue(),
                            weekStart.atTime(0, 0),
                            weekEnd.atTime(0, 0),
                            0
                            );
                    })
                .sorted(Comparator.comparing(a -> a.getBegindate())) // Tri par date
                .collect(Collectors.toList());

    }

    public static ArrayList<ConsumptionResponseDTO> aggregateByMonth(ArrayList<Consumption> records) {
        return (ArrayList<ConsumptionResponseDTO>)  records.stream()
                .collect(Collectors.groupingBy(
                        r -> YearMonth.from(r.getDate()), // Regroupement par mois
                        Collectors.summingDouble(Consumption::getNbkwh) // Somme des KwH
                ))
                .entrySet().stream()
                .map(entry -> new ConsumptionResponseDTO(
                        entry.getValue(),
                        entry.getKey().atDay(1).atTime(0, 0),
                        entry.getKey().atEndOfMonth().atTime(0, 0),
                        0
                ))
                .sorted(Comparator.comparing(ConsumptionResponseDTO::getBegindate)) // Tri par date
                .collect(Collectors.toList());

    }

    public ArrayList<ConsumptionResponseDTO> aggregate(ArrayList<Consumption> lstConsumption, String filterType){
        ArrayList<ConsumptionResponseDTO> lstDTO;
        int numSlice=1;
        //selon le type du filtre, aggrégat différent
        switch (filterType.toLowerCase()){
            case("h"):
                lstDTO=aggregateByHour(lstConsumption);
                break;
            case("d"):
                lstDTO=aggregateByDay(lstConsumption);
                break;
            case("w"):
                lstDTO=aggregateByWeek(lstConsumption);
                break;
            case("m"):
                lstDTO=aggregateByMonth(lstConsumption);
                break;
            default:
                throw new BusinessLogicException("cas d'aggrégat non géré --> "+filterType);
        }

        for (ConsumptionResponseDTO item:lstDTO){
            if (item.getNumSlice()==0){item.setNumSlice(numSlice++);}
        }
        return lstDTO;
    }

    public ArrayList<ConsumptionResponseDTO> findGoodList(FilterDTO filterDTO){
        ArrayList<Consumption> lstEntity;
        lstEntity = consumptionReporitory.findByReservationInOrderByDateAsc
                (reservationService.getReservationByFilter(filterDTO));

        return aggregate(lstEntity, filterDTO.getType());

    }

    public ArrayList<ConsumptionResponseDTO> getByFilter(FilterDTO filterDTO){

        ArrayList<ConsumptionResponseDTO> lstDTO = findGoodList(filterDTO);
        if(lstDTO.isEmpty()){
            throw new ResourceNotFoundException("Aucune consommation ne correspond à votre sélection");
        }

        return lstDTO;
    }

    public String createConsumption(ConsumptionDTO consumptionDTO){
        Consumption consumption= consumptionReporitory.save(consumptionMapper.ConsumptionDTOToEntity(consumptionDTO));
        return "Votre consommation en date du "+consumptionDTO.getDate()+ " a bien été enregistrée";
    }
}
