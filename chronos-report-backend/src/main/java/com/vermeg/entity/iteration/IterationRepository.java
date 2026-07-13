package com.vermeg.entity.iteration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface IterationRepository extends JpaRepository<Iteration, Long> {
    Optional<Iteration> findByName(String name);
}