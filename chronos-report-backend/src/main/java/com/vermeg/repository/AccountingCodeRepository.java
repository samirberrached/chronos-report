package com.vermeg.repository;

import com.vermeg.model.AccountingCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountingCodeRepository extends JpaRepository<AccountingCode, Long> {
    // Assure-toi que c'est bien écrit AccountingCode ici et pas AccountCode ou autre !
}