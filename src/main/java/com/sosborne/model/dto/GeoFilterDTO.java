package com.sosborne.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GeoFilterDTO {
    private double userLat; //latitude de l'utilisateur géolocalisé
    private double userlong; //longitude de l'utilisateur géolocalisé
    private double userKm; //distance en kilomètres choisie par l'utilisateur
}
