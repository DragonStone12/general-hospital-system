package com.pam.patientservice.entity;

import com.pam.patientservice.constraints.UniquePatientColumn;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(name = "patients_details")
public class PatientDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @UniquePatientColumn(message = "MRN must be unique")
    @Column(name = "mrn")
    @NotBlank(message = "MRN cannot be empty.")
    @NaturalId
    private String mrn;

    @Column(name = "email")
    @Email(message = "Please enter a valid email address")
    @UniquePatientColumn(message = "Email must be unique")
    @NaturalId
    private String email;

    @Version
    @Column
    private Integer version;

    @Column(name = "first_name")
    @NotBlank(message = "First name cannot be blank.")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    @NotBlank(message = "Last name cannot be blank.")
    private String lastName;

    @Column(name = "preferred_name")
    private String preferredName;

    @NotNull(message = "Phone number field cannot be empty")
    @Column(name = "phone")
    private String phone;

    @NotNull
    @Column(name = "is_active")
    private Boolean isActive = true;

    // TODO: Replace with @CreatedDate when Spring Data JPA auditing is configured
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // TODO: Replace with @LastModifiedDate when Spring Data JPA auditing is configured
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // TODO: Replace with @CreatedBy when Spring Data JPA auditing is configured
    @Column(name = "created_by")
    private String createdBy;

    // TODO: Replace with @LastModifiedBy when Spring Data JPA auditing is configured
    @Column(name = "updated_by")
    private String updatedBy;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "patientDetails")
    private Set<PatientAddress> patientAddresses = new HashSet<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "patientDetails")
    private Set<PatientVisit> patientVisits = new HashSet<>();

    // TODO: Remove these methods when Spring Data JPA auditing is implemented
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addAddress(PatientAddress address) {
        this.patientAddresses.add(address);
        address.setPatientDetails(this);
    }

    public void removeAddress(PatientAddress address) {
        address.setPatientDetails(null);
        this.patientAddresses.remove(address);
    }

    public void removeAllAddresses() {
        this.patientAddresses.forEach(address -> address.setPatientDetails(null));
        this.patientAddresses.clear();
    }

    public void addPatientVisit(PatientVisit visit) {
        this.patientVisits.add(visit);
        visit.setPatientDetails(this);
    }

    public void removePatientVisit(PatientVisit visit) {
        visit.setPatientDetails(null);
        this.patientVisits.remove(visit);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (Objects.isNull(o) || getClass() != o.getClass()) return false;

        PatientDetails p = (PatientDetails) o;

        return Objects.nonNull(p.id) && Objects.equals(p.id, this.id);
    }

    @Override
    public int hashCode() {
        return Objects.nonNull(this.id) ? Objects.hash(this.id, this.mrn, this.email) : 0;
    }

}




