package com.vermeg.entity.activitynature;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ActivityNatureRepository extends JpaRepository<ActivityNature, Long> {
    Optional<ActivityNature> findByName(String name);
}