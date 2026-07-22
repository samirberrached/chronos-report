package com.vermeg.classification.calendar;

import java.time.LocalDate;

public interface WorkingDaysService {

    int countWorkingDays(String countryCode, LocalDate start, LocalDate end);
}
