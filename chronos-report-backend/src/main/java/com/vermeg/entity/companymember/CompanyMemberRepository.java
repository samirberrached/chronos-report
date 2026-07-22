package com.vermeg.entity.companymember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CompanyMemberRepository extends JpaRepository<CompanyMember, Long> {

    @Query("SELECT cm FROM CompanyMember cm " +
            "WHERE cm.startDate <= :monthEnd " +
            "AND (cm.endDate IS NULL OR cm.endDate >= :monthStart) " +
            "ORDER BY cm.employee.id, cm.company.id")
    List<CompanyMember> findActiveForPeriod(@Param("monthStart") LocalDate monthStart, @Param("monthEnd") LocalDate monthEnd);
}