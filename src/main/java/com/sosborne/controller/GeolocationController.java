package com.sosborne.controller;

import com.sosborne.model.dto.GeoBorneDTO;
import com.sosborne.model.dto.GeoFilterDTO;
import com.sosborne.service.GeolocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/geo")
public class GeolocationController {
    private final GeolocationService geolocationService;


    @GetMapping("/get/geo")
    public ResponseEntity<ArrayList<GeoBorneDTO>> geoBorne(@RequestBody GeoFilterDTO filterDTO){
        return ResponseEntity.status(HttpStatus.FOUND).body(geolocationService.borneGeolocation(filterDTO));
    }
}
