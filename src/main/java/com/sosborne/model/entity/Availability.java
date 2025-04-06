package com.sosborne.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "availability")
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "beginhour", nullable = false)
    private LocalTime beginhour;

    @NotNull
    @Column(name = "endhour", nullable = false)
    private LocalTime endhour;

    @NotNull
    @Column(name = "kwhprice", nullable = false)
    private Double kwhprice;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "borneid", nullable = false)
    private Borne borne;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "dayid", nullable = false)
    private Day day;


}
