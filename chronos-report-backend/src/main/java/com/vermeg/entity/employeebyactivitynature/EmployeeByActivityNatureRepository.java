package com.vermeg.entity.employeebyactivitynature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeByActivityNatureRepository extends JpaRepository<EmployeeByActivityNature, Long> {

    @Query("SELECT ean FROM EmployeeByActivityNature ean " +
            "LEFT JOIN FETCH ean.employee " +
            "LEFT JOIN FETCH ean.activityNature")
    List<EmployeeByActivityNature> findAllWithAssociations();
}
