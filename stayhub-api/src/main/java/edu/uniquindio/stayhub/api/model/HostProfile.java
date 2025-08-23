package edu.uniquindio.stayhub.api.model;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

@Entity
@Table(name = "host_profiles")
public class HostProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;

    @ElementCollection
    @Column(nullable = true)
    @URL(message = "Cada documento debe tener una URL válida")
    private List<String> legalDocuments;

    @OneToOne(mappedBy = "hostProfile")
    private User user;
}