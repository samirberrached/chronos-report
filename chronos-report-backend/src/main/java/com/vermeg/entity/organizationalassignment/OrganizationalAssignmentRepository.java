package com.vermeg.entity.organizationalassignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationalAssignmentRepository extends JpaRepository<OrganizationalAssignment, Long> {

    @Query("SELECT oa FROM OrganizationalAssignment oa " +
            "LEFT JOIN FETCH oa.employee " +
            "LEFT JOIN FETCH oa.organizationalUnit " +
            "LEFT JOIN FETCH oa.product " +
            "LEFT JOIN FETCH oa.accountingCode ac " +
            "LEFT JOIN FETCH ac.activityNature")
    List<OrganizationalAssignment> findAllWithAssociations();
}
