package com.vermeg.entity.phase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface PhaseRepository extends JpaRepository<Phase, Long> {
    Optional<Phase> findByName(String name);
}