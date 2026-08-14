package com.fluxa.repository;

import com.fluxa.model.Doctor;
import com.fluxa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findByOwnerOrderByCreatedAtDesc(User owner);
    Optional<Doctor> findByIdAndOwner(Long id, User owner);
    long countByOwner(User owner);
    long countByOwnerAndStatus(User owner, String status);
}
