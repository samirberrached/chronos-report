package com.vermeg.entity.employeebyactivitynature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface EmployeeByActivityNatureRepository extends JpaRepository<EmployeeByActivityNature, Long> {
}