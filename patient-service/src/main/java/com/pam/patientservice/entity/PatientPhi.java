package com.pam.patientservice.entity;

import com.pam.patientservice.constants.GenderType;
import com.pam.patientservice.constants.PreferredLanguageType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "patients_phi")
public class PatientPhi {

    @Id
    private Long id;

    @Version
    @Column
    private Integer version = 0;

    @NotNull(message = "Gender field cannot be empty.")
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private GenderType gender;

    @NotNull(message = "Date of birth is required")
    @PastOrPresent(message = "Date of birth cannot be in the future")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @NotNull(message = "Please specify a preferred language")
    @Column(name = "preferred_language")
    @Enumerated(EnumType.STRING)
    private PreferredLanguageType preferredLanguage;

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

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private PatientDetails patientDetails;

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
}


