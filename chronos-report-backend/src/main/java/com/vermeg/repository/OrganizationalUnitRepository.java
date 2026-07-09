package com.vermeg.repository;

import com.vermeg.model.OrganizationalUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OrganizationalUnitRepository extends JpaRepository<OrganizationalUnit, Long> {
    Optional<OrganizationalUnit> findByName(String name);
}