package com.sosborne.service;


import com.sosborne.model.entity.Day;
import com.sosborne.repository.DayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DayService {
    private final DayRepository dayRepository;

    public Day getByNum(int numDay){

        String tabDay[] = new String[7];
        tabDay[0]= "lundi";
        tabDay[1]= "mardi";
        tabDay[2]= "mercredi";
        tabDay[3]= "jeudi";
        tabDay[4]= "vendredi";
        tabDay[5]= "samedi";
        tabDay[6]= "dimanche";

        String name = tabDay[numDay-1];
        Day day = dayRepository.findByDay(name);

        return day;
    }

}
