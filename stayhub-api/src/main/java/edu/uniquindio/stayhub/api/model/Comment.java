package edu.uniquindio.stayhub.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Comment {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}