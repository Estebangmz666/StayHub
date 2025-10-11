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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.URL;

import java.util.List;

/**
 * Entity representing a host's profile.
 * This class maps to the 'host_profiles' table and contains specific information
 * related to a user who is a host, such as a personal description and
 * a list of legal documents.
 */
@Entity
@Table(name = "host_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class HostProfile extends Auditable{

    /**
     * The unique identifier for the host profile.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * A personal description or bio provided by the host.
     */
    @Column
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;

    /**
     * A collection of URLs pointing to legal documents provided by the host.
     */
    @ElementCollection
    @Column
    @URL(message = "Cada documento debe tener una URL válida")
    private List<String> legalDocuments;

    /**
     * The user associated with this host profile.
     * This is a one-to-one relationship mapped by the 'hostProfile' field in the User entity.
     */
    @OneToOne(mappedBy = "hostProfile")
    private User user;
}