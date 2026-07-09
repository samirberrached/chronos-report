package com.vermeg.repository;

import com.vermeg.model.BillingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BillingEntityRepository extends JpaRepository<BillingEntity, Long> {
    Optional<BillingEntity> findByName(String name);
}