package com.sosborne.controller;

import com.sosborne.model.dto.ConsumptionDTO;
import com.sosborne.model.dto.ConsumptionResponseDTO;
import com.sosborne.model.dto.FilterDTO;
import com.sosborne.service.ConsumptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/consos")
public class ConsumptionController {
    private final ConsumptionService consumptionService;

    @GetMapping("/get/conso")
    public ResponseEntity<ArrayList<ConsumptionResponseDTO>> suiviConso(@RequestBody FilterDTO filterDTO){
        return ResponseEntity.status(HttpStatus.FOUND).body(consumptionService.getByFilter(filterDTO));
    }

    @PostMapping("/add")
    public String newConso(@RequestBody ConsumptionDTO consumptionDTO){
        return consumptionService.createConsumption(consumptionDTO);
    }
}
