package com.pam.patientservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patient")
@Slf4j
public class PatientController {

    @GetMapping
    public ResponseEntity<String> getPatients() {
        return ResponseEntity.ok("Patient class is still working");
    }

    @PostMapping
    public ResponseEntity<String> createPatient() {
        return ResponseEntity.status(HttpStatus.CREATED).body("Created");
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getPatientById(@PathVariable(name = "id") Long id) {
        log.info("id" + id);
        return ResponseEntity.ok("Got him");
    }
}
