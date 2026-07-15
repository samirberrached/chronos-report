package com.company.chronos.service;

import com.company.chronos.dto.DashboardResponse;

/**
 * Service contract for the aggregated dashboard overview consumed by Power BI
 * and the future frontend.
 */
public interface DashboardService {

    /**
     * Builds the dashboard overview for the current reporting month.
     *
     * @return the aggregated dashboard response
     */
    DashboardResponse getDashboard();
}