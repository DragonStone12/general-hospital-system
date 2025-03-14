package com.pam.patientservice.repository;

import com.pam.patientservice.entity.PatientDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<PatientDetails, Long> {

    boolean existsByMrn(String mrn);
}
