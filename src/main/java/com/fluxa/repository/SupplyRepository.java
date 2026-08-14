package com.fluxa.repository;

import com.fluxa.model.Supply;
import com.fluxa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SupplyRepository extends JpaRepository<Supply, Long> {
    List<Supply> findByOwnerOrderByCreatedAtDesc(User owner);
    Optional<Supply> findByIdAndOwner(Long id, User owner);
    long countByOwner(User owner);
}
