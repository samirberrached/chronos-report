package com.company.chronos.repository;

import com.company.chronos.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link User} entities, used by the
 * authentication layer to load principals by email.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique email.
     *
     * @param email the user's email
     * @return the matching user, if any
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a user with the given email already exists.
     *
     * @param email the email to check
     * @return true if a user exists
     */
    boolean existsByEmail(String email);
}