package com.vermeg.repository;
import com.vermeg.model.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface LotRepository extends JpaRepository<Lot, Long> {
    Optional<Lot> findByName(String name);
}