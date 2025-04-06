package com.sosborne.service;



import com.sosborne.exception.GeoLocationException;
import com.sosborne.model.dto.AddressDTO;
import com.sosborne.model.dto.AvailabilityDTO;
import com.sosborne.model.dto.GeoBorneDTO;
import com.sosborne.model.dto.GeoFilterDTO;

import com.fasterxml.jackson.databind.*;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;


@RequiredArgsConstructor
@Service
public class GeolocationService {
    private static final Logger logger = Logger.getLogger(GeolocationService.class.getName());
    private final BorneService borneService;
    private final RestTemplate restTemplate;


    //gestion des doublons dans le fichier json des bornes publiques (comparaison avec ce qui est déjà chargé dans la liste)
    public Boolean doublon(ArrayList<GeoBorneDTO> lstBornes, double longitude, double latitude){
        boolean found = false;
        for(GeoBorneDTO item:lstBornes){
            if((item.getLongitude()==longitude) && (item.getLatitude()==latitude)){found=true;}
        }

        return found;
    }

    public static boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }

    public AddressDTO  getTownInsee(String insee){
        //Appel à l'API du gouvernement pour récupérer nom et code postal à partir du code insee
        //exemple:<https://geo.api.gouv.fr/communes?code=44109
        AddressDTO addressDTO = new AddressDTO();
        try{
            String encodedInsee = URLEncoder.encode(insee, StandardCharsets.UTF_8);
            String urlInsee = "https://geo.api.gouv.fr/communes?code=" + encodedInsee;
            logger.info("Requête envoyée à odre.opendatasoft : " + urlInsee);
            //Appeler l'API
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    urlInsee,
                    HttpMethod.GET,
                    null,
                    String.class
            );
            //récupérer la réponse au format string
            String responseInsee = responseEntity.getBody();
            if (responseInsee != null && !responseInsee.isEmpty()) {
                //convertir la réponse en JSon
                ObjectMapper objectMapper = new ObjectMapper();
                try{
                    JsonNode jsonArray = objectMapper.readTree(responseInsee);
                    addressDTO.setTown(jsonArray.get(0).get("nom").asText());;
                    //Je prends le premier code postal de la liste renvoyée
                    addressDTO.setZipcode(jsonArray.get(0).get("codesPostaux").get(0).asText());
                } catch (Exception e) {
                    throw new GeoLocationException("Erreur de geo.api.gouv.fr/communes: code insee --> "+insee
                            +"\n erreur: "+e);
                }
            }
        } catch (Exception e) {
            logger.severe("Erreur lors de la recherche du code INSEE --> "+insee+" erreur: " + e.getMessage());
        }

        return addressDTO;
    }

    public AddressDTO buildAddressDTO(String adresse, String insee){
        AddressDTO addressDTO= new AddressDTO();
        boolean trouve = false;
        String street="";
        String zipcode="";
        String town="";
        int indAddr;

        adresse= adresse.trim();
        String[] partsAdr = adresse.split(" ");
        //recherche code postale dans l'adresse
        for (int i = partsAdr.length - 1; i >= 0; i--) {
            if (isNumeric(partsAdr[i].trim()) && !trouve) {
                zipcode = partsAdr[i].trim();
                trouve = true;
            }
        }
        if (trouve) {
            indAddr = adresse.indexOf(zipcode);
            street = adresse.substring(0, indAddr).trim();
            town = adresse.substring(indAddr + 5).trim();
        } else {
            // retrouver la ville et zipcode via le code insee
            street = adresse;// charge toute l'info même si redondance avec les autres champs
            AddressDTO foundDTO = getTownInsee(insee);
            town = foundDTO.getTown();
            zipcode = foundDTO.getZipcode();
        }
        addressDTO.setStreet(street);
        addressDTO.setZipcode(zipcode);
        addressDTO.setTown(town);
        return addressDTO;
    }


    public ArrayList<GeoBorneDTO> bornePublicExtract(GeoFilterDTO geoFilter){
        ArrayList<GeoBorneDTO> bornesP= new ArrayList<>();
        ArrayList<AvailabilityDTO> lstEmpty= new ArrayList<>();
        //gestion des doublons
        String wAdr="";

        String response = "";
        String geolocation = Double.toString(geoFilter.getUserlong())+" "+ Double.toString(geoFilter.getUserLat());
        int nbBornesP= 0;
        boolean payant= true;

        try{
            //Préparer la requète d'appel de l'API. Exemple de requète:
            //https://odre.opendatasoft.com/api/explore/v2.1/catalog/datasets/bornes-irve/records?select=*&where=within_distance(geo_point_borne,geom'POINT(3.07089400291%2050.6308326721)',10km)&limit=-1
            String urlgeo = "https://odre.opendatasoft.com/api/explore/v2.1/catalog/datasets/bornes-irve/records?select=*&where=within_distance(geo_point_borne,geom'POINT("
                            + geolocation + ")',"+geoFilter.getUserKm()+ "km)&limit=-1";
            logger.info("Requête envoyée à odre.opendatasoft : \"" + urlgeo + "\"");
            //Appeler l'API
            try {
                ResponseEntity<String> responseEntity = restTemplate.exchange(
                        urlgeo,
                        HttpMethod.GET,
                        null,
                        String.class
                );
                logger.info("Réponse API : " + responseEntity.getBody());
                //récupérer la réponse au format string
                response = responseEntity.getBody();
            } catch (Exception e) {
                logger.severe("Erreur lors de l'appel API : " + e.getMessage());
            }

            if (response != null && !response.isEmpty()) {
                //convertir la réponse en JSon
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode rootNode = objectMapper.readTree(response);
                    JsonNode results = rootNode.get("results");
                    if (results != null && results.isArray()) {
                        // Vérifier s'il y a un message d'erreur
                        if (!rootNode.has("error")) {
                            //pour chaque noeud créer un GeoBorneDTO et l'ajouter à la liste.
                            for (JsonNode result : results) {
                                //gestion des doublons
                                if (!doublon(bornesP,result.get("xlongitude").asDouble(), result.get("ylatitude").asDouble())) {
                                    //dernier test des doublons sur l'adresse
                                    if (!result.get("ad_station").asText().toLowerCase().equals(wAdr)) {
                                        wAdr = result.get("ad_station").asText().toLowerCase();

                                        if (result.get("acces_recharge").asText().toLowerCase().equals("payant")) {
                                            payant = true;
                                        } else {
                                            payant = false;
                                        }

                                        bornesP.add(new GeoBorneDTO(
                                                    result.get("n_enseigne").asText(),
                                                    result.get("n_station").asText(),
                                                    buildAddressDTO(result.get("ad_station").asText(), result.get("code_insee").asText()),
                                                1, result.get("ylatitude").asDouble(),
                                                    result.get("xlongitude").asDouble(),
                                                    result.get("puiss_max").asDouble(),
                                                    result.get("type_prise").asText(),
                                                    payant, true,
                                                    result.get("nbre_pdc").asInt(),
                                                true,
                                                    result.get("accessibilite").asText(),
                                                    lstEmpty
                                                )
                                        );
                                    }
                                }
                            }
                        } else {
                            logger.warning("Erreur de Geocode.xyz : " + rootNode.get("error")
                                    + "geofilter: " + Double.toString(geoFilter.getUserLat()) + " "
                                    + Double.toString(geoFilter.getUserlong())
                                    + " " + Double.toString(geoFilter.getUserKm()) + "km");
                        }
                    }
                } catch (Exception e) {
                    logger.severe("Erreur lors de la conversion en JSon de la réponse de l'API : " + e.getMessage()
                                    +"\n réponse brute --> "+response);
                }
            }
        }catch (Exception e) {
            logger.severe("Erreur lors de la géolocalisation : " + e.getMessage());
        }

        return bornesP;
    }

    public ArrayList<GeoBorneDTO> borneGeolocation(GeoFilterDTO geoFilter){
        //récupération des bornes publiques
        ArrayList<GeoBorneDTO> lstBorne = bornePublicExtract(geoFilter);
        System.out.println("Nombre de bornes publiques à géolocaliser: "+Integer.toString(lstBorne.size()));
        //extraction des bornes privées et ajout à la liste
        return borneService.getByGeoFilter(geoFilter, lstBorne);

    }
}
