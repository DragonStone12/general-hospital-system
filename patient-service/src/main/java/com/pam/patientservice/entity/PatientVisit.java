package com.pam.patientservice.entity;

import com.pam.patientservice.constants.VisitStatus;
import com.pam.patientservice.constants.VisitType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "patient_visits")
public class PatientVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_details_id")
    private PatientDetails patientDetails;

    @NotNull
    @Column(name = "provider_id")
    private Long providerId;

    @NotNull
    @Column(name = "facility_id")
    private Long facilityId;

    @NotNull
    @Column(name = "appointment_id")
    private Long appointmentId;

    @Version
    @Column
    private Integer version;

    @Column(name = "visit_type")
    @NotNull(message = "Visit type cannot be blank.")
    @Enumerated(EnumType.STRING)
    private VisitType visitType;

    @Column(name = "visit_status")
    @NotNull(message = "Visit status cannot be blank.")
    @Enumerated(EnumType.STRING)
    private VisitStatus visitStatus;

    @NotNull
    @Column(name = "visit_date")
    private LocalDateTime visitDate;

    @Column(name = "chief_complaint")
    @NotBlank(message = "Chief complaint cannot be empty")
    private String chiefComplaint;

    @NotNull
    @Column(name = "has_review")
    private Boolean hasReview = false;
}
