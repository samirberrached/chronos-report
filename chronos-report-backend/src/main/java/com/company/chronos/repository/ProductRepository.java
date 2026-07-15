package com.company.chronos.repository;

import com.company.chronos.entity.Company;
import com.company.chronos.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Product} entities.
 *
 * <p>Provides lookup by business key and company-scoped retrieval.</p>
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Finds a product by its business key within a company.
     *
     * @param productCode the product code
     * @param company the owning company
     * @return the matching product, if any
     */
    Optional<Product> findByProductCodeAndCompany(String productCode, Company company);

    /**
     * Returns all products belonging to a company.
     *
     * @param company the owning company
     * @return the list of products
     */
    List<Product> findAllByCompany(Company company);
}