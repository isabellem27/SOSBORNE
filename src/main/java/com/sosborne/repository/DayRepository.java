package com.sosborne.repository;


import com.sosborne.model.entity.Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DayRepository extends JpaRepository<Day, UUID> {

        Day findByDay(String name);

}
