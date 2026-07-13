package com.vermeg.entity.organizationalassignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface OrganizationalAssignmentRepository extends JpaRepository<OrganizationalAssignment, Long> {
}