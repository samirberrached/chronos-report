package com.vermeg.entity.employeetime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface EmployeeTimeRepository extends JpaRepository<EmployeeTime, Long> {
}