package com.company.chronos.repository;

import com.company.chronos.entity.Calendar;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Calendar} entities.
 *
 * <p>Supports lookups by date and range queries used to normalise time entries
 * against working days.</p>
 */
@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    /**
     * Finds the calendar row for a specific date.
     *
     * @param date the calendar date
     * @return the matching calendar entry, if any
     */
    Optional<Calendar> findByDate(LocalDate date);

    /**
     * Returns all calendar entries whose date falls within the inclusive range.
     *
     * @param start the start date (inclusive)
     * @param end the end date (inclusive)
     * @return the list of calendar entries in range
     */
    @Query("SELECT c FROM Calendar c WHERE c.date BETWEEN :start AND :end ORDER BY c.date")
    List<Calendar> findAllBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    /**
     * Returns the count of working days in a given reporting month (yyyy-MM).
     *
     * @param month the reporting month
     * @return the number of working days
     */
    @Query("SELECT COUNT(c) FROM Calendar c WHERE c.month = :month AND c.workingDay = true")
    long countWorkingDaysByMonth(@Param("month") String month);
}