package com.fluxa.repository;

import com.fluxa.model.Patient;
import com.fluxa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByOwnerOrderByCreatedAtDesc(User owner);
    Optional<Patient> findByIdAndOwner(Long id, User owner);
    long countByOwner(User owner);
    long countByOwnerAndStatus(User owner, String status);
}
