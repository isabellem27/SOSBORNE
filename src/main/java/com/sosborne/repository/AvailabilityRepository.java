package com.sosborne.repository;


import com.sosborne.model.entity.Availability;
import com.sosborne.model.entity.Borne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.UUID;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, UUID> {

    ArrayList<Availability> findByBorne(Borne borne);

    void deleteByBorne(Borne borne);

}
