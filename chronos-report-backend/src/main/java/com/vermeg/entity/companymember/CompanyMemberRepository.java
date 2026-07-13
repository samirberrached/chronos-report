package com.vermeg.entity.companymember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CompanyMemberRepository extends JpaRepository<CompanyMember, Long> {
}