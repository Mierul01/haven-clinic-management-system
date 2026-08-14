package com.fluxa.repository;

import com.fluxa.model.Prescription;
import com.fluxa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByOwnerOrderByCreatedAtDesc(User owner);
    Optional<Prescription> findByIdAndOwner(Long id, User owner);
    long countByOwnerAndStatus(User owner, String status);
}
