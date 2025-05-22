package com.ownervoting.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "house")
public class House {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    @Column(length = 50)
    private String building;

    @Column(length = 50)
    private String unit;

    @Column(length = 50)
    private String room;

    @Column(length = 255)
    private String address;

    private BigDecimal area;

    @Column(length = 100)
    private String certificateNumber;

    private Boolean isPrimary = false;

    public Owner getOwner() {
        return owner;
    }
} 