package com.pam.patientservice.entity;

import com.pam.patientservice.constants.AddressType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "patient_addresses")
public class PatientAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column
    private Integer version;

    @JoinColumn(name = "patient_details_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PatientDetails patientDetails;

    @Column(name = "address_type")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Address type cannot be empty.")
    private AddressType addressType;

    @Column(name = "address_line1")
    @NotBlank(message = "Address cannot be empty.")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column
    @NotBlank(message = "City field cannot be empty.")
    private String city;

    @Column
    @NotBlank(message = "State field cannot be empty.")
    private String state;

    @Column
    @NotBlank(message = "Zip field cannot be empty.")
    private String zipcode;

    @Column(name = "is_primary")
    @NotNull(message = "isPrimary field cannot be empty.")
    private Boolean isPrimary = false;

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
