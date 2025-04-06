package com.sosborne.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Size(max = 150)
    @NotNull
    @Column(name = "street", nullable = false, length = 150)
    private String street;

    @Size(max = 10)
    @NotNull
    @Column(name = "zipcode", nullable = false, length = 10)
    private String zipcode;

    @Size(max = 100)
    @NotNull
    @Column(name = "town", nullable = false, length = 100)
    private String town;

    @Size(max = 10)
    @Column(name = "insee", length = 10)
    private String insee;

    @NotNull
    @Column(name = "addresslat", nullable = false)
    private Double addresslat;

    @NotNull
    @Column(name = "addresslong", nullable = false)
    private Double addresslong;

}
