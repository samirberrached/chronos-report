package com.vermeg.repository;
import com.vermeg.model.CompanyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CompanyMemberRepository extends JpaRepository<CompanyMember, Long> {
}