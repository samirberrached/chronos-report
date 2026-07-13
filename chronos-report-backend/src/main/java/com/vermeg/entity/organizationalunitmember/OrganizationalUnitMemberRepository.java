package com.vermeg.entity.organizationalunitmember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface OrganizationalUnitMemberRepository extends JpaRepository<OrganizationalUnitMember, Long> {
}