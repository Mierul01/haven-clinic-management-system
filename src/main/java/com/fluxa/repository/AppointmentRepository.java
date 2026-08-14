package com.fluxa.repository;

import com.fluxa.model.Appointment;
import com.fluxa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByOwnerOrderByAppointmentDateDescAppointmentTimeDesc(User owner);
    Optional<Appointment> findByIdAndOwner(Long id, User owner);
    long countByOwnerAndStatus(User owner, String status);
    long countByOwnerAndAppointmentDate(User owner, LocalDate date);
}
