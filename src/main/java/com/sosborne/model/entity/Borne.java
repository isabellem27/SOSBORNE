package com.sosborne.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "borne")
public class Borne {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "numborne", nullable = false,columnDefinition = "INTEGER DEFAULT 1")
    private Integer numborne = 1;

    @NotNull
    @Column(name = "power")
    private Double power;

    @Size(max = 10)
    @Column(name = "type", length = 10)
    private String type;

    @Size(max = 100)
    @Column(name = "taughtname", length = 100)
    private String taughtname;

    @Size(max = 100)
    @Column(name = "marketname", length = 100)
    private String marketname;

    @NotNull
    @Column(name = "public", nullable = false,columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean publicField = false;

    @NotNull
    @Column(name = "working", nullable = false,columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean working = true;

    @NotNull
    @Column(name = "active", nullable = false,columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean active = true;

    @NotNull
    @Column(name = "nbpoint", nullable = false,columnDefinition = "INTEGER DEFAULT 1")
    private Integer nbpoint = 1;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;
}
