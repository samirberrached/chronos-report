package com.vermeg.repository;
import com.vermeg.model.OrganizationalAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface OrganizationalAssignmentRepository extends JpaRepository<OrganizationalAssignment, Long> {
}