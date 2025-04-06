package com.sosborne.repository;


import com.sosborne.model.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    Address findByStreetAndZipcodeAndTown(String address,String zipCode,String town);

    @Query(value = " SELECT * FROM address WHERE "+
            "(6371 * 2 * ATAN2( SQRT( SIN(RADIANS(addresslat - :lat1) / 2) * SIN(RADIANS(addresslat - :lat1) / 2)"+
            "+ COS(RADIANS(:lat1)) * COS(RADIANS(addresslat)) * SIN(RADIANS(addresslong - :lon1) / 2) "+
            "* SIN(RADIANS(addresslong - :lon1) / 2) ), SQRT(1 - ( SIN(RADIANS(addresslat - :lat1) / 2) " +
            "* SIN(RADIANS(addresslat - :lat1) / 2) + COS(RADIANS(:lat1)) * COS(RADIANS(addresslat)) "+
            "* SIN(RADIANS(addresslong - :lon1) / 2) * SIN(RADIANS(addresslong - :lon1) / 2) )) )) <= :radius ", nativeQuery = true)
    ArrayList<Address> findAddressWithinDistance(@Param("lat1") double lat1, @Param("lon1") double lon1, @Param("radius") double radius);

}
