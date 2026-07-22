package com.vermeg.entity.employeebyproduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeByProductRepository extends JpaRepository<EmployeeByProduct, Long> {

    @Query("SELECT ebp FROM EmployeeByProduct ebp " +
            "LEFT JOIN FETCH ebp.employee " +
            "LEFT JOIN FETCH ebp.product")
    List<EmployeeByProduct> findAllWithAssociations();
}
