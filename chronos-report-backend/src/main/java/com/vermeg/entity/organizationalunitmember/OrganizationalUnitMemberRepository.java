package com.vermeg.entity.organizationalunitmember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationalUnitMemberRepository extends JpaRepository<OrganizationalUnitMember, Long> {

    @Query("SELECT oum FROM OrganizationalUnitMember oum " +
            "LEFT JOIN FETCH oum.employee " +
            "LEFT JOIN FETCH oum.organizationalUnit ou " +
            "LEFT JOIN FETCH ou.parent")
    List<OrganizationalUnitMember> findAllWithAssociations();
}
