package com.vermeg.entity.employeetime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeeTimeRepository extends JpaRepository<EmployeeTime, Long> {

    @Query("SELECT new com.vermeg.entity.employeetime.EmployeeTimeProjection(" +
            "et.employee.id, et.date, et.manDay, " +
            "ac.operationalIdentifier, an.name, ou.name, p.name) " +
            "FROM EmployeeTime et " +
            "JOIN et.activity a " +
            "JOIN a.phase ph " +
            "JOIN ph.accountingCode ac " +
            "LEFT JOIN ac.activityNature an " +
            "LEFT JOIN ac.organizationalUnit ou " +
            "LEFT JOIN ac.product p " +
            "WHERE et.date BETWEEN :periodStart AND :periodEnd")
    List<EmployeeTimeProjection> findProjectionsByDateBetween(@Param("periodStart") LocalDate periodStart,
                                                               @Param("periodEnd") LocalDate periodEnd);
}
