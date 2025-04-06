package com.sosborne.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50)
    private String name;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 50)
    private String firstname;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(max = 50)
    private String password;

    private AddressDTO address;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Size(max = 20)
    private String phone;

    @Size(max = 200)
    private String photourl;

    @Size(max = 200)
    private String idpieceurl;
}
