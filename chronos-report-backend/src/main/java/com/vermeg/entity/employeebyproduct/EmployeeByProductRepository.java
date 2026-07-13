package com.vermeg.entity.employeebyproduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface EmployeeByProductRepository extends JpaRepository<EmployeeByProduct, Long> {
}