package com.fluxa.repository;

import com.fluxa.model.Room;
import com.fluxa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByOwnerOrderByCreatedAtDesc(User owner);
    Optional<Room> findByIdAndOwner(Long id, User owner);
    long countByOwnerAndStatus(User owner, String status);
}
