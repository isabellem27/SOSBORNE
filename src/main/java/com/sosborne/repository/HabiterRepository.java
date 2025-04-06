package com.sosborne.repository;

import com.sosborne.model.entity.Address;
import com.sosborne.model.entity.Habiter;
import com.sosborne.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HabiterRepository extends JpaRepository<Habiter, UUID> {
    ArrayList<Habiter> findByUser(User user);
    Optional<Habiter> findByAddress(Address address);
}
