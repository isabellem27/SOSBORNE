package com.sosborne.repository;


import com.sosborne.model.entity.Address;
import com.sosborne.model.entity.Borne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BorneRepository extends JpaRepository<Borne, UUID> {
    ArrayList<Borne> findAllByAddress(Address address);

    ArrayList<Borne> findByAddressAndActiveTrue(Address address);

    Optional<Borne> findByAddressAndActiveTrueAndNumborne(Address address, int numBorne);

    Integer countByAddress(Address address);

    ArrayList<Borne> findByActiveTrueAndWorkingTrueAndAddressIn(ArrayList<Address> lstAddress);

}
