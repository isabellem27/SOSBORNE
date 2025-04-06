package com.sosborne.config;

public final class MessageConstants {

    private MessageConstants() {}

    // Messages de succès
    public static final String CREATION_SUCCESS = "Création effectuée avec succès.";
    public static final String UPDATE_SUCCESS = "Mise à jour effectuée avec succès.";
    public static final String DELETION_SUCCESS = "Suppression effectuée avec succès.";
    public static final String FETCH_SUCCESS = "Données récupérées avec succès.";

    // Messages d'erreur
    public static final String EMAIL_ALREADY_USED = "L'email est déjà utilisé.";
    public static final String GEOLOCATION_FAILED = "Impossible de géolocaliser l'adresse. Merci de la vérifier.";
}
