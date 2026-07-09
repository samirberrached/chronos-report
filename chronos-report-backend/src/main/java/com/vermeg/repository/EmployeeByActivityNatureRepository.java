package com.vermeg.repository;
import com.vermeg.model.EmployeeByActivityNature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface EmployeeByActivityNatureRepository extends JpaRepository<EmployeeByActivityNature, Long> {
}