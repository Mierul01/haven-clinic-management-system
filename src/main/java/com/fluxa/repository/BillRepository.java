package com.fluxa.repository;

import com.fluxa.model.Bill;
import com.fluxa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByOwnerOrderByCreatedAtDesc(User owner);
    Optional<Bill> findByIdAndOwner(Long id, User owner);
    long countByOwner(User owner);

    @Query("select coalesce(sum(b.amount), 0) from Bill b where b.owner = ?1 and b.status = 'PAID'")
    BigDecimal sumPaidByOwner(User owner);

    @Query("select coalesce(sum(b.amount), 0) from Bill b where b.owner = ?1 and b.status in ('PENDING','OVERDUE')")
    BigDecimal sumOutstandingByOwner(User owner);
}
