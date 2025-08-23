package edu.uniquindio.stayhub.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Accommodation {

    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private User host;
}