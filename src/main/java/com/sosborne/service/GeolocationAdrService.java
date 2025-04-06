package com.sosborne.service;



import com.sosborne.exception.GeoLocationException;
import com.sosborne.exception.ResourceNotFoundException;
import com.sosborne.model.dto.AddressDTO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Service
public class GeolocationAdrService {

    //--------------------------------------------------------------------------------------------------------------------//
    // Eclatement du service de géolocalisation pour éviter la détection de boucle dans l'appel des services à la compile
    //--------------------------------------------------------------------------------------------------------------------//

    private static final Logger logger = Logger.getLogger(GeolocationAdrService.class.getName());
    private final RestTemplate restTemplate;

    //géolocalisation de l'adresse
    public Map<String, Double> addressGeolocation(AddressDTO address) {
        String response = "";
        double lat = 0;
        double lon = 0;

        //l'api gère les noms de rue avec l'ortographe complète et sans virgule après le numéro
        address.setStreet(address.getStreet().replace(",","")); //suppression de la virgule
        if (address.getStreet().contains(" bd") || address.getStreet().contains(" Bd")) {
            address.setStreet(address.getStreet().replace(" bd"," boulevard"));
            address.setStreet(address.getStreet().replace(" Bd"," boulevard"));
        }else if (address.getStreet().contains(" av") || address.getStreet().contains(" Av")
                    || address.getStreet().contains(" av.") || address.getStreet().contains(" Av.")){
            address.setStreet(address.getStreet().replace(" av"," avenue"));
            address.setStreet(address.getStreet().replace(" Av"," avenue"));
            address.setStreet(address.getStreet().replace(" av."," avenue"));
            address.setStreet(address.getStreet().replace(" Av."," avenue"));
        }

        // Encoder l'adresse pour l'insérer dans l'URL
        //exemple fonctionnant: https://api-adresse.data.gouv.fr/search/?q=8+bd+du+port&postcode=44380&city=Pornichet
        String encodedAddress = address.getStreet().replace(" ", "+")
                + "&postcode=" + address.getZipcode()
                + "&city=" + address.getTown();

        // Construire l'URL
        String url = "https://api-adresse.data.gouv.fr/search/?q=" + encodedAddress;
        logger.info("Requête envoyée à api-adresse.data.gouv.fr : " + url);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    String.class
            );

            response = responseEntity.getBody();
        } catch (Exception e) {
            logger.severe("Erreur lors de l'appel api-adresse.data.gouv.fr : " + e.getMessage());
        }

        if (response != null && !response.isEmpty()) {
            // Afficher la réponse brute pour debug
            logger.info("Réponse brute de api-adresse.data.gouv.fr : " + response);
            try {
                // Conversion de la réponse JSON
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response);
                // Vérifier s'il y a un message d'erreur
                if (rootNode.has("error")) {
                    throw new GeoLocationException("Erreur api-adresse.data.gouv.fr: " + rootNode.get("error").asText());
                }
                JsonNode features = rootNode.get("features");
                if (features != null && features.isArray() && features.size() > 0) {
                    //on prend le premier retour d'adresse --> géolocalisation de l'adresse ou de la rue
                    JsonNode properties = features.get(0).get("properties");
                    JsonNode geometry = features.get(0).get("geometry");
                    JsonNode coordinates = geometry.get("coordinates");
                    if (properties != null && geometry != null && coordinates != null) {
                        //et on contrôle que l'adresse complète est identique
                        //if (properties.get("name").asText().equals(address.getStreet())
                        //et on contrôle que l'adresse contient la réponse
                        // cas où numéro pas correct ==> retour = nom rue uniquement
                        if (address.getStreet().toLowerCase().contains(properties.get("name").asText().toLowerCase())
                                && properties.get("postcode").asText().equals(address.getZipcode())
                                && properties.get("city").asText().equals(address.getTown())) {
                            // je prends les coordonnées pour les renvoyer
                            lat = coordinates.get(1).asDouble();
                            lon = coordinates.get(0).asDouble();
                            logger.info("Adresse trouvée : latitude=" + lat + ", longitude=" + lon);
                        } else {
                            throw new ResourceNotFoundException("Adresse impossible à géolocaliser --> "
                                    + address.getStreet() + " " + address.getZipcode() + " " + address.getTown());
                        }
                    }
                }else{
                    throw new GeoLocationException("addresse impossible à géolocaliser (introuvable) --> "
                            + address.getStreet() + " " + address.getZipcode() + " " + address.getTown());
                }

            } catch (Exception e) {
                throw new GeoLocationException("Problème de lecture JSon de la réponse de l'API adresse --> "
                                                +"\n"+ response + " " +e);
            }
        }

        return Map.of("latitude", lat, "longitude", lon);
    }


}
