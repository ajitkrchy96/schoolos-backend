package com.school.service.dashboard;

import com.school.dto.dashboard.DashboardResponseDTO;

public interface DashboardService {

    DashboardResponseDTO getDashboard(Long schoolId);
}