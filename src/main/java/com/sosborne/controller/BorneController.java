package com.sosborne.controller;

import com.sosborne.model.dto.*;
import com.sosborne.service.BorneService;
import com.sosborne.service.ConsumptionService;
import com.sosborne.service.GeolocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bornes")
public class BorneController {

    private final BorneService borneService;
    private final ConsumptionService consumptionService;
    private final GeolocationService geolocationService;

    @GetMapping("/get/geo")
    public ResponseEntity<ArrayList<GeoBorneDTO>> geoBorne(@RequestBody GeoFilterDTO filterDTO){
        return ResponseEntity.status(HttpStatus.FOUND).body(geolocationService.borneGeolocation(filterDTO));
    }

    @GetMapping("/get/conso")
    public ResponseEntity<ArrayList<ConsumptionResponseDTO>> suiviConso(@RequestBody FilterDTO filterDTO){
        return ResponseEntity.status(HttpStatus.FOUND).body(consumptionService.getByFilter(filterDTO));
    }

    @GetMapping("/get/all")
    public ResponseEntity<ArrayList<BorneDTO>> getAllBorne(@RequestBody AddressDTO addressDTO){
        return ResponseEntity.status(HttpStatus.FOUND).body(borneService.getAllBorneDTO(addressDTO));
    }


    @GetMapping("/get/{numBorne}")
    public ResponseEntity<BorneDTO> getBorne(@RequestBody  AddressDTO addressDTO, @PathVariable int numBorne){
        return ResponseEntity.status(HttpStatus.FOUND).body(borneService.getBorneDTO(addressDTO, numBorne));
    }

    @PostMapping("/add")
    public String newBorne(@RequestBody  BorneDTO borneDTO){
        return borneService.createBorne(borneDTO);
    }

    @PatchMapping("/upd")
    public ResponseEntity<BorneDTO> updBorne(@RequestBody  BorneDTO borneDTO){
        return ResponseEntity.status(HttpStatus.FOUND).body(borneService.updateBorne(borneDTO));
    }

    @PatchMapping("/del/{numBorne}")
    public String delBorne(@RequestBody AddressDTO addressDTO, @PathVariable int numBorne){
        return borneService.inactiveBorne(addressDTO, numBorne);
    }

}
