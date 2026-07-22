package com.vermeg.classification.calendar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Working-day calculator backed by the public Nager.Date holiday API.
 * No country calendar table is maintained locally; holidays are fetched
 * per country/year and cached in memory for the lifetime of the app.
 */
@Service
public class NagerDateWorkingDaysService implements WorkingDaysService {

    private static final String API_URL = "https://date.nager.at/api/v3/PublicHolidays/%d/%s";

    private final RestTemplate restTemplate = new RestTemplate();
    private final Map<String, Set<LocalDate>> holidaysByCountryYear = new ConcurrentHashMap<>();

    @Value("${chronos.classification.default-country-code:TN}")
    private String defaultCountryCode;

    @Override
    public int countWorkingDays(String countryCode, LocalDate start, LocalDate end) {
        String country = (countryCode == null || countryCode.isBlank()) ? defaultCountryCode : countryCode;

        Set<LocalDate> holidays = new HashSet<>();
        for (int year = start.getYear(); year <= end.getYear(); year++) {
            holidays.addAll(getHolidays(country, year));
        }

        int count = 0;
        for (LocalDate day = start; !day.isAfter(end); day = day.plusDays(1)) {
            boolean isWeekend = day.getDayOfWeek() == DayOfWeek.SATURDAY || day.getDayOfWeek() == DayOfWeek.SUNDAY;
            if (!isWeekend && !holidays.contains(day)) {
                count++;
            }
        }
        return count;
    }

    private Set<LocalDate> getHolidays(String countryCode, int year) {
        String key = countryCode + "-" + year;
        return holidaysByCountryYear.computeIfAbsent(key, k -> fetchHolidays(countryCode, year));
    }

    private Set<LocalDate> fetchHolidays(String countryCode, int year) {
        try {
            String url = String.format(API_URL, year, countryCode);
            NagerHoliday[] holidays = restTemplate.getForObject(url, NagerHoliday[].class);
            if (holidays == null) return Set.of();
            return Arrays.stream(holidays)
                    .map(h -> LocalDate.parse(h.date()))
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            System.err.println("⚠️ Impossible de récupérer les jours fériés (" + countryCode + "/" + year + ") : " + e.getMessage());
            return Set.of();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record NagerHoliday(String date) {}
}
