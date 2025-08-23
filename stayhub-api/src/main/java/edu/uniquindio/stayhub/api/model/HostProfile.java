package edu.uniquindio.stayhub.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Entity
@Table(name = "host_profiles")
@Getter @Setter
public class HostProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;

    @ElementCollection
    @Column
    @URL(message = "Cada documento debe tener una URL válida")
    private List<String> legalDocuments;

    @OneToOne(mappedBy = "hostProfile")
    private User user;
}